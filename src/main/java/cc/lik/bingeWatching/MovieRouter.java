package cc.lik.bingeWatching;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.finders.HandsomeMovieFinder;
import cc.lik.bingeWatching.service.SettingConfigGetter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.theme.TemplateNameResolver;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MovieRouter {
    private final TemplateNameResolver templateNameResolver;
    private final HandsomeMovieFinder movieFinder;
    private final SettingConfigGetter settingConfigGetter;

    @Bean
    RouterFunction<ServerResponse> movieRouterFunction() {
        return route(GET("/movies"), this::renderMoviePage)
            .andRoute(GET("/movies/{name}"), this::renderMovieDetailPage);
    }

    private Mono<ServerResponse> renderMoviePage(ServerRequest request) {
        return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "movie-wall")
            .flatMap(templateName -> ServerResponse.ok().render(templateName,
                Map.of(
                    "title", getPosterWallTitle(),
                    "movies", movieFinder.listAll()
                )));
    }

    private Mono<ServerResponse> renderMovieDetailPage(ServerRequest request) {
        String name = request.pathVariable("name");
        return movieFinder.getByMetadataName(name)
            .next()
            .flatMap(movie -> {
                Map<String, Object> model = new HashMap<>();
                model.put("movie", movie);
                return getPosterWallTitle()
                    .map(title -> title + "|" + movie.getSpec().getVod_name())
                    .flatMap(formattedTitle -> {
                        model.put("title", formattedTitle);
                        return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "movie-detail")
                            .flatMap(templateName -> ServerResponse.ok()
                                .render(templateName, model));
                    });
            });
    }

    Mono<String> getPosterWallTitle() {
        return this.settingConfigGetter.getBasicConfig()
            .map(SettingConfigGetter.BasicConfig::getTitle)
            .defaultIfEmpty("海报墙");
    }
} 