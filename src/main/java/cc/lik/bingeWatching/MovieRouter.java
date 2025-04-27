package cc.lik.bingeWatching;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.finders.HandsomeMovieFinder;
import cc.lik.bingeWatching.service.SettingConfigGetter;
import java.util.HashMap;
import java.util.Map;

import cc.lik.bingeWatching.vo.HandsomeMovieVo;
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
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Bean
    RouterFunction<ServerResponse> movieRouterFunction() {
        return route(GET("/movies"), this::renderMoviePage)
            .andRoute(GET("/movies/{name}"), this::renderMovieDetailPage);
    }

    private Mono<ServerResponse> renderMoviePage(ServerRequest request) {
        String keyword = request.queryParam("keyword").orElse(null);
        int page = Integer.parseInt(request.queryParam("page").orElse("1"));
        int size = Integer.parseInt(request.queryParam("size").orElse(String.valueOf(DEFAULT_PAGE_SIZE)));
        
        return settingConfigGetter.getBasicConfig().flatMap(config ->
            templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "movie-wall")
                .flatMap(templateName -> {
                    Mono<run.halo.app.extension.ListResult<HandsomeMovieVo>> moviesMono = 
                        (keyword != null && !keyword.isBlank())
                            ? movieFinder.listFuzzySearchByName(page, size, keyword)
                            : movieFinder.list(page, size);
                    
                    return moviesMono.flatMap(listResult -> {
                        Map<String, Object> model = new HashMap<>();
                        model.put("title", config.getTitle());
                        model.put("description", config.getDescription());
                        model.put("erDescription", config.getErDescription());
                        model.put("enableNavigationBar", config.getEnableNavigationBar());
                        model.put("movies", listResult.getItems());
                        model.put("currentPage", listResult.getPage());
                        model.put("totalPages", (int) Math.ceil((double) listResult.getTotal() / size));
                        model.put("totalCount", listResult.getTotal());
                        
                        return ServerResponse.ok().render(templateName, model);
                    });
                })
        );
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