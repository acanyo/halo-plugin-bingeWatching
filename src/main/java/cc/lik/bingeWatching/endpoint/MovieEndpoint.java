package cc.lik.bingeWatching.endpoint;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;

import io.swagger.v3.oas.annotations.enums.ParameterIn;

import cc.lik.bingeWatching.MovieQuery;
import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.service.ProvideService;
import cc.lik.bingeWatching.service.FileAttachmentService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.endpoint.CustomEndpoint;
import run.halo.app.extension.GroupVersion;
import run.halo.app.extension.ListResult;
import run.halo.app.core.extension.attachment.Attachment;

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieEndpoint implements CustomEndpoint {
    private final ProvideService provideSvc;
    private final FileAttachmentService fileAttachmentService;
    
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
            .GET("movies/-/{name}", this::getMovieByName, builder -> {
                    builder.operationId("Get movie by name")
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
                        );
                }
            )
            .POST("movies/attachment", this::updateFile, builder -> {
                    builder.operationId("Update attachment")
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
                        );
                }
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
            .flatMap(result -> {
                return ServerResponse.ok().bodyValue(result);
            })
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

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("api.bingewatching.lik.cc/v1alpha1");
    }
}
