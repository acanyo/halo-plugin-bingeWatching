package cc.lik.bingeWatching.finders;

import cc.lik.bingeWatching.vo.HandsomeMovieVo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListResult;

public interface HandsomeMovieFinder {

    Flux<HandsomeMovieVo> listAll();

    Mono<ListResult<HandsomeMovieVo>> list(Integer page, Integer size);

    Mono<HandsomeMovieVo> getByName(String movieName);

    Mono<ListResult<HandsomeMovieVo>> listByName(Integer page, Integer size,String movieName);
}
