package cc.lik.bingeWatching;

import static java.util.Comparator.comparing;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static run.halo.app.extension.index.query.QueryFactory.contains;
import static run.halo.app.extension.index.query.QueryFactory.equal;
import static run.halo.app.extension.index.query.QueryFactory.or;
import static run.halo.app.extension.router.QueryParamBuildUtil.sortParameter;

import cc.lik.bingeWatching.entity.HandsomeMovie;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.function.server.ServerRequest;
import run.halo.app.core.extension.endpoint.SortResolver;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.PageRequest;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.router.IListRequest;
import run.halo.app.extension.router.SortableRequest;

public class MovieQuery extends SortableRequest {


    public MovieQuery(ServerRequest request) {
        super(request.exchange());
    }

    @Nullable
    public String getKeyword() {
        return StringUtils.defaultIfBlank(queryParams.getFirst("keyword"), null);
    }
    @Nullable
    public String getTimelineType() {
        return queryParams.getFirst("type");
    }

    public ListOptions toListOptions() {
        var builder = ListOptions.builder(super.toListOptions());

        Optional.ofNullable(getKeyword())
            .filter(StringUtils::isNotBlank)
            .ifPresent(keyword -> builder.andQuery(or(
                contains("spec.vod_name", keyword),
                contains("spec.vod_content", keyword),
                contains("spec.vod_en", keyword),
                contains("metadata.name", keyword)
            )));

        Optional.ofNullable(getTimelineType())
            .filter(StringUtils::isNotBlank)
            .ifPresent(type -> builder.andQuery(equal("spec.status", type)));
        return builder.build();
    }

    public Comparator<HandsomeMovie> toComparator() {
        List<Comparator<HandsomeMovie>> comparators = new ArrayList<>();
        var sort = getSort();
        var ctOrder = sort.getOrderFor("createTime");
        if (ctOrder != null) {
            Comparator<HandsomeMovie> comparator =
                comparing(timeline -> timeline.getMetadata().getCreationTimestamp());
            if (ctOrder.isDescending()) {
                comparator = comparator.reversed();
            }
            comparators.add(comparator);
        }
        Comparator<HandsomeMovie> defaultComparator =
            comparing(timeline -> timeline.getMetadata().getCreationTimestamp());
        comparators.add(defaultComparator.reversed());
        return comparators.stream()
            .reduce(Comparator::thenComparing)
            .orElse(null);
    }


    public Sort getSort() {
        var sort = SortResolver.defaultInstance.resolve(exchange);
        return sort.and(Sort.by("metadata.creationTimestamp").descending());
    }

    public PageRequest toPageRequest() {
        return PageRequestImpl.of(getPage(), getSize(), getSort());
    }

    public static void buildParameters(Builder builder) {
        IListRequest.buildParameters(builder);
        builder.parameter(sortParameter())
            .parameter(parameterBuilder()
                .in(ParameterIn.QUERY)
                .name("keyword")
                .description("timeline filtered by keyword.")
                .implementation(String.class)
                .required(false));
    }


} 