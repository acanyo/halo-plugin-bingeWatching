package cc.lik.bingeWatching.endpoint;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import cc.lik.bingeWatching.MovieQuery;
import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.service.ProvideService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class MovieEndpoint implements CustomEndpoint {
    private final ProvideService provideSvc;
    @Override
    public RouterFunction<ServerResponse> endpoint() {
        final var tag = "api.handsomemovie.lik.cc/v1alpha1/movies";
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
            .GET("movies/{name}", this::getMovieByName, builder -> {
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

    @Override
    public GroupVersion groupVersion() {
        return GroupVersion.parseAPIVersion("api.handsomemovie.lik.cc/v1alpha1");
    }
}
