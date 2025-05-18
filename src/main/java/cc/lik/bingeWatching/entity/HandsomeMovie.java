package cc.lik.bingeWatching.entity;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;
import java.util.List;

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
        private String vodName;
        @Schema(description = "英文名称")
        private String vodEn;
        @Schema(description = "影视图片", requiredMode = REQUIRED)
        private String vodPic;
        @Schema(description = "影视演员")
        private String vodActor;
        @Schema(description = "地区语言")
        private String vodLang;
        @Schema(description = "影视年份")
        private String vodYear;
        @Schema(description = "影视评分")
        private String vodScore;
        @Schema(description = "影视描述")
        private String vodContent;
        @Schema(description = "影视类型")
        private String typeName;
        @Schema(description = "已看集数")
        private String seen;
        @Schema(description = "更新集数")
        private String newSeen = "0";
        @Schema(description = "更新周期/周")
        private String updateCycle;
        @Schema(description = "追剧状态")
        private String status;
        @Schema(description = "经典台词")
        private List<String> classicLines;
    }
}
