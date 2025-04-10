package cc.lik.bingeWatching.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import java.util.Set;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "HandsomeMovie", group = "bingewatching.lik.cc",
    version = "v1alpha1", singular = "handsomemovie", plural = "handsomemovie")
public class HandsomeMovie extends AbstractExtension {
    @Schema(requiredMode = REQUIRED)
    private HandsomeMovieSpec spec;
    @Data
    public static class HandsomeMovieSpec {

        private String name;
        private String poster;
        private String link;
        private String id;
        private String score;
        private String year;
        private String type;
        private String pubdate;
        private String cardSubtitle;
        private String dataType;

        private Set<String> genres;

    }
}
