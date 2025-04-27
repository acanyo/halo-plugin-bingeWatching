package cc.lik.bingeWatching.service;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface SettingConfigGetter {
    Mono<BasicConfig> getBasicConfig();
    Mono<StyleConfig> getStyleConfig();


    @Data
    class BasicConfig {
        public static final String GROUP = "basic";
        private Boolean enablePicDump;
        private String fileGroup;
        private String filePolicy;
        private Boolean isProxy;
        private String proxyHost;
        private Boolean cronUpdate;
    }
    @Data
    class StyleConfig {
        public static final String GROUP = "style";
        private String title;
        private String themeColor;
        private String titleColor;
        private String descriptionColor;
        private String description;
        private String navLogo;
        private String erDescription;
        private Boolean enableNavigationBar;
    }
}
