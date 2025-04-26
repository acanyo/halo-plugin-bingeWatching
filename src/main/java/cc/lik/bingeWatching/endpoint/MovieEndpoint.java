package cc.lik.bingeWatching.endpoint;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;

import cc.lik.bingeWatching.MovieQuery;
import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.job.CronUpdateDataJob;
import cc.lik.bingeWatching.service.FileAttachmentService;
import cc.lik.bingeWatching.service.ProvideService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.attachment.Attachment;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieEndpoint implements CustomEndpoint {
    private final ProvideService provideSvc;
    private final FileAttachmentService fileAttachmentService;
    private final ObjectMapper objectMapper;
    private final CronUpdateDataJob cronUpdateDataJob;
    
    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = "api.bingewatching.lik.cc/v1alpha1/bingewatching";
        return SpringdocRouteBuilder.route()
            .GET("movies", this::listMovie, builder -> {
                    builder.operationId("List movies")
                        .tag(tag)
                        .description("分页查询")
                        .response(
                            responseBuilder()
                                .implementation(ListResult.generateGenericClass(HandsomeMovie.class))
                        );
                MovieQuery.buildParameters(builder);
                }
            )
            .GET("movies/-/{name}", this::getMovieByName, builder -> builder.operationId("Get movie by name")
                .tag(tag)
                .description("根据名称获取电影")
                .parameter(
                    parameterBuilder()
                        .in(ParameterIn.PATH)
                        .name("name")
                        .description("电影名称")
                        .required(true)
                )
                .response(
                    responseBuilder()
                        .implementation(JsonNode.class)
                )
            )
            .POST("movies/insert", this::insertMovies, builder -> builder.operationId("Insert movies")
                .tag(tag)
                .description("批量插入影视记录")
                .requestBody(
                    requestBodyBuilder()
                        .required(true)
                        .description("影视记录列表")
                        .content(
                            contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                                .schema(schemaBuilder()
                                    .implementation(HandsomeMovie[].class)
                                    .description("""
                                        影视记录数组，每条记录包含：
                                        - vod_name: 影视名称
                                        - updateCycle: 更新周期
                                        - seen: 已看集数
                                        - vod_pic: 影视图片
                                        - vod_actor: 演员
                                        - vod_lang: 语言
                                        - vod_year: 年份
                                        - vod_score: 评分
                                        - vod_content: 简介
                                        - type_name: 类型
                                        """)
                                )
                        )
                )
                .response(
                    responseBuilder()
                        .implementation(Void.class)
                )
            )
            .POST("movies/attachment", this::updateFile, builder -> builder.operationId("Update attachment")
                .tag(tag)
                .description("转存附件")
                .parameter(
                    parameterBuilder()
                        .in(ParameterIn.QUERY)
                        .name("picUrl")
                        .description("图片URL")
                        .required(true)
                )
                .response(
                    responseBuilder()
                        .implementation(Attachment.class)
                )
            )
            .POST("movies/sync", this::syncMovies, builder -> builder.operationId("Sync movies")
                .tag(tag)
                .description("手动同步电影集数")
                .response(
                    responseBuilder()
                        .implementation(Void.class)
                )
            )
            .build();
    }

    Mono<ServerResponse> listMovie(ServerRequest serverRequest) {
        MovieQuery movieQuery = new MovieQuery(serverRequest);
        return provideSvc.listMovie(movieQuery)
            .flatMap(movies -> ServerResponse.ok().bodyValue(movies));
    }

    Mono<ServerResponse> getMovieByName(ServerRequest serverRequest) {
        String name = serverRequest.pathVariable("name");
        return provideSvc.getProvideMovieList(name)
            .flatMap(result -> ServerResponse.ok().bodyValue(result))
            .doOnError(error -> log.error("handsome-halo-plugin-bingeWatching: 获取电影失败, 电影名称: {}", name, error))
            .onErrorResume(error -> ServerResponse.badRequest().bodyValue("获取电影失败: " + error.getMessage()));
    }

    Mono<ServerResponse> updateFile(ServerRequest serverRequest) {
        String picUrl = serverRequest.queryParam("picUrl").orElse("");
        if (picUrl.isEmpty()) {
            return ServerResponse.badRequest().bodyValue("图片URL不能为空");
        }
        
        return fileAttachmentService.updateFile(picUrl)
            .flatMap(attachment -> ServerResponse.ok().bodyValue(attachment))
            .doOnError(error -> log.error("handsome-halo-plugin-bingeWatching: 转存附件失败", error))
            .onErrorResume(error -> ServerResponse.badRequest().bodyValue("转存附件失败: " + error.getMessage()));
    }

    Mono<ServerResponse> insertMovies(ServerRequest request) {
        return request.bodyToMono(String.class)
            .flatMap(body -> {
                try {
                    List<HandsomeMovie> movies = objectMapper.readValue(body,
                        new TypeReference<>() {
                        });
                    return provideSvc.insertMovie(movies)
                        .then(ServerResponse.ok().build());
                } catch (Exception e) {
                    return Mono.error(new RuntimeException("解析请求数据失败: " + e.getMessage()));
                }
            })
            .doOnError(error -> log.error("批量插入影视记录失败", error))
            .onErrorResume(error -> ServerResponse.badRequest()
                .bodyValue("批量插入影视记录失败: " + error.getMessage()));
    }

    Mono<ServerResponse> syncMovies(ServerRequest request) {
        return Mono.fromRunnable(cronUpdateDataJob::update)
            .then(ServerResponse.ok().build())
            .doOnError(error -> log.error("手动同步电影集数失败", error))
            .onErrorResume(error -> ServerResponse.badRequest()
                .bodyValue("手动同步电影集数失败: " + error.getMessage()));
    }

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("api.bingewatching.lik.cc/v1alpha1");
    }
}
