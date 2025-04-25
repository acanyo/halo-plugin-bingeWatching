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
    version = "v1alpha1", singular = "handsomemovie", plural = "handsomemovies")
public class HandsomeMovie extends AbstractExtension {
    @Schema(requiredMode = REQUIRED)
    private HandsomeMovieSpec spec;
    @Data
    public static class HandsomeMovieSpec {
        @Schema(description = "影视名称", requiredMode = REQUIRED)
        private String vod_name;
        @Schema(description = "英文名称")
        private String vod_en;
        @Schema(description = "影视图片", requiredMode = REQUIRED)
        private String vod_pic;
        @Schema(description = "影视演员")
        private String vod_actor;
        @Schema(description = "地区语言")
        private String vod_lang;
        @Schema(description = "影视年份")
        private String vod_year;
        @Schema(description = "影视评分")
        private String vod_score;
        @Schema(description = "影视描述")
        private String vod_content;
        @Schema(description = "影视类型")
        private String type_name;
        @Schema(description = "已看集数")
        private String seen;
        @Schema(description = "更新周期/周")
        private String updateCycle;

    }
}
