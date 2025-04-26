package cc.lik.bingeWatching;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.theme.TemplateNameResolver;

@Configuration
@RequiredArgsConstructor
public class MovieRouter {
    private final TemplateNameResolver templateNameResolver;

    @Bean
    RouterFunction<ServerResponse> movieRouterFunction() {
        return route(GET("/movies"), this::renderMoviePage)
            .andRoute(GET("/movies/{name}"), this::renderMovieDetailPage);
    }

    private Mono<ServerResponse> renderMoviePage(ServerRequest request) {
        Map<String, Object> model = new HashMap<>();
        return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "movie-wall")
            .flatMap(templateName -> ServerResponse.ok()
                .render(templateName, model));
    }

    private Mono<ServerResponse> renderMovieDetailPage(ServerRequest request) {
        String name = request.pathVariable("name");
        Map<String, Object> model = new HashMap<>();
        model.put("name", name);
        return templateNameResolver.resolveTemplateNameOrDefault(request.exchange(), "movie-detail")
            .flatMap(templateName -> ServerResponse.ok()
                .render(templateName, model));
    }
} 