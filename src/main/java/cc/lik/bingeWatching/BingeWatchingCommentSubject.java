package cc.lik.bingeWatching;

import cc.lik.bingeWatching.entity.HandsomeMovie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import run.halo.app.content.comment.CommentSubject;
import run.halo.app.extension.GroupVersionKind;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Ref;
import run.halo.app.infra.ExternalLinkProcessor;

/**
 * 追剧插件评论主体展示
 * @author: webjing
 * @date: 2025年04月28日 21:23
 */
@Component
@RequiredArgsConstructor
public class BingeWatchingCommentSubject implements CommentSubject<HandsomeMovie> {

    private final ReactiveExtensionClient client;

    private final ExternalLinkProcessor externalLinkProcessor;

    @Override
    public Mono<HandsomeMovie> get(String name) {
        return client.get(HandsomeMovie.class, name);
    }

    @Override
    public Mono<SubjectDisplay> getSubjectDisplay(String name) {
        return get(name)
            .map(handsomeMovie -> {
                var url = externalLinkProcessor
                    .processLink("/movies/" + handsomeMovie.getMetadata().getName());
                return new SubjectDisplay(handsomeMovie.getSpec().getVod_name(), url, "追剧");
            });
    }

    @Override
    public boolean supports(Ref ref) {
        Assert.notNull(ref, "Subject ref must not be null.");
        GroupVersionKind
            groupVersionKind = new GroupVersionKind(ref.getGroup(), ref.getVersion(), ref.getKind());
        return GroupVersionKind.fromExtension(HandsomeMovie.class).equals(groupVersionKind);
    }

}
