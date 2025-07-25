package cc.lik.bingeWatching.service;

import cc.lik.bingeWatching.MovieQuery;
import cc.lik.bingeWatching.entity.HandsomeMovie;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;

public interface ProvideService {
    Mono<JsonNode> getProvideMovieList(String vodName);
    Mono<ListResult<HandsomeMovie>> listMovie(MovieQuery query);
    Mono<Void> insertMovie(List<HandsomeMovie> movies);
}
