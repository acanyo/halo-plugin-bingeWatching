package cc.lik.bingeWatching.finders.impl;

import static org.springframework.data.domain.Sort.Order.asc;
import static run.halo.app.extension.index.query.QueryFactory.all;
import static run.halo.app.extension.index.query.QueryFactory.and;
import static run.halo.app.extension.index.query.QueryFactory.equal;

import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.finders.HandsomeMovieFinder;
import cc.lik.bingeWatching.vo.HandsomeMovieVo;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.PageRequest;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.router.selector.FieldSelector;
import run.halo.app.theme.finders.Finder;


@Finder("handsomeMovieFinder")
@RequiredArgsConstructor
public class HandsomeMovieFinderImpl implements HandsomeMovieFinder {

    private final ReactiveExtensionClient client;



    @Override
    public Flux<HandsomeMovieVo> listAll() {
        var listOptions = new ListOptions();
        var query = all();
        listOptions.setFieldSelector(FieldSelector.of(query));
        return client.listAll(HandsomeMovie.class, listOptions, defaultSort())
            .flatMap(this::getHandsomeMovieVo);
    }

    @Override
    public Mono<ListResult<HandsomeMovieVo>> list(Integer page, Integer size) {
        var pageRequest = PageRequestImpl.of(pageNullSafe(page), sizeNullSafe(size), defaultSort());
        return pageHandsomeMoviePost(null, pageRequest);
    }


    @Override
    public Mono<HandsomeMovieVo> getByName(String vod_name) {
        return client.fetch(HandsomeMovie.class, vod_name)
            .map(HandsomeMovieVo::from);
    }

    @Override
    public Mono<ListResult<HandsomeMovieVo>> listByName(Integer page, Integer size,String name) {
        var query = equal("spec.name", name);
        var pageRequest = PageRequestImpl.of(pageNullSafe(page), sizeNullSafe(size), defaultSort());
        return pageHandsomeMoviePost(FieldSelector.of(query), pageRequest);
    }


    private Mono<ListResult<HandsomeMovieVo>> pageHandsomeMoviePost(FieldSelector fieldSelector, PageRequest page){
        var listOptions = new ListOptions();
        var query = all();
        if (fieldSelector != null) {
            query = and(query, fieldSelector.query());
        }
        listOptions.setFieldSelector(FieldSelector.of(query));
        return client.listBy(HandsomeMovie.class, listOptions, page)
            .flatMap(list -> Flux.fromStream(list.get())
                .concatMap(this::getHandsomeMovieVo)
                .collectList()
                .map(movieVos -> new ListResult<>(list.getPage(), list.getSize(),
                    list.getTotal(), movieVos)
                )
            )
            .defaultIfEmpty(
                new ListResult<>(page.getPageNumber(), page.getPageSize(), 0L, List.of()));

    }

    static Sort defaultLinkSort() {
        return Sort.by(asc("spec.priority"),
            asc("metadata.creationTimestamp"),
            asc("metadata.name")
        );
    }

    static Sort defaultSort() {
        return Sort.by("spec.createTime").descending();
    }

    private Mono<HandsomeMovieVo> getHandsomeMovieVo(@Nonnull HandsomeMovie movie) {
        HandsomeMovieVo movieVo = HandsomeMovieVo.from(movie);
        return Mono.just(movieVo);
    }

    int pageNullSafe(Integer page) {
        return ObjectUtils.defaultIfNull(page, 1);
    }

    int sizeNullSafe(Integer size) {
        return ObjectUtils.defaultIfNull(size, 10);
    }


}
