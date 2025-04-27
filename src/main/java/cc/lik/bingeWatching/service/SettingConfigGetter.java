package cc.lik.bingeWatching.service;

import lombok.Data;
import reactor.core.publisher.Mono;

public interface SettingConfigGetter {
    Mono<BasicConfig> getBasicConfig();


    @Data
    class BasicConfig {
        public static final String GROUP = "basic";
        private String title;
        private String description;
        private String erDescription;
        private Boolean enablePicDump;
        private Boolean enableNavigationBar;
        private String fileGroup;
        private String filePolicy;
        private Boolean isProxy;
        private String proxyHost;
        private Boolean cronUpdate;
    }
}
