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
    public String getStatus() {
        return queryParams.getFirst("status");
    }

    public ListOptions toListOptions() {
        var builder = ListOptions.builder(super.toListOptions());

        Optional.ofNullable(getKeyword())
            .filter(StringUtils::isNotBlank)
            .ifPresent(keyword -> builder.andQuery(or(
                contains("spec.vodName", keyword),
                contains("spec.vodContent", keyword),
                contains("spec.vodEn", keyword),
                contains("metadata.name", keyword)
            )));

        Optional.ofNullable(getStatus())
            .filter(StringUtils::isNotBlank)
            .ifPresent(type -> builder.andQuery(equal("spec.status", type)));
        return builder.build();
    }
    public static void buildParameters(Builder builder) {
        IListRequest.buildParameters(builder);
        builder.parameter(sortParameter())
            .parameter(parameterBuilder()
                .in(ParameterIn.QUERY)
                .name("keyword")
                .description("bingeWatching filtered by keyword.")
                .implementation(String.class)
                .required(false))
            .parameter(parameterBuilder()
                .in(ParameterIn.QUERY)
                .name("status")
                .description("BingeWatching status.")
                .implementation(String.class)
                .required(false));
    }

    public Sort getSort() {
        var sort = SortResolver.defaultInstance.resolve(exchange);
        if (sort.isEmpty()) {
            return Sort.by("spec.newSeen").descending();
        }
        return sort;
    }

    public Comparator<HandsomeMovie> toComparator() {
        List<Comparator<HandsomeMovie>> comparators = new ArrayList<>();
        var sort = getSort();
        
        // 处理更新集数排序
        var newSeenOrder = sort.getOrderFor("spec.newSeen");
        if (newSeenOrder != null) {
            Comparator<HandsomeMovie> comparator = comparing(movie -> {
                String newSeen = movie.getSpec().getNewSeen();
                return newSeen != null && !newSeen.trim().isEmpty() ? Integer.parseInt(newSeen) : 0;
            });
            if (newSeenOrder.isDescending()) {
                comparator = comparator.reversed();
            }
            comparators.add(comparator);
        }
        
        // 默认按更新集数倒序
        Comparator<HandsomeMovie> defaultComparator = comparing(movie -> {
            String newSeen = movie.getSpec().getNewSeen();
            return newSeen != null && !newSeen.trim().isEmpty() ? Integer.parseInt(newSeen) : 0;
        });
        comparators.add(defaultComparator.reversed());
        
        return comparators.stream()
            .reduce(Comparator::thenComparing)
            .orElse(null);
    }

} 