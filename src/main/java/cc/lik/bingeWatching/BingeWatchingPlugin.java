package cc.lik.bingeWatching;

import static run.halo.app.extension.index.IndexAttributeFactory.simpleAttribute;

import cc.lik.bingeWatching.entity.HandsomeMovie;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.extension.index.IndexSpec;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
public class BingeWatchingPlugin extends BasePlugin {
    private final SchemeManager schemeManager;
    public BingeWatchingPlugin(PluginContext pluginContext, SchemeManager schemeManager) {
        super(pluginContext);
        this.schemeManager = schemeManager;
    }

    @Override
    public void start() {
        schemeManager.register(HandsomeMovie.class, indexSpecs -> {
            indexSpecs.add(new IndexSpec()
                .setName("spec.vod_name")
                .setIndexFunc(
                    simpleAttribute(HandsomeMovie.class,
                        movie -> movie.getSpec().getVod_name())));
            indexSpecs.add(new IndexSpec()
                .setName("spec.vod_en")
                .setIndexFunc(
                    simpleAttribute(HandsomeMovie.class,
                        movie -> movie.getSpec().getVod_en())));
            indexSpecs.add(new IndexSpec()
                .setName("spec.status")
                .setIndexFunc(
                    simpleAttribute(HandsomeMovie.class,
                        movie -> movie.getSpec().getStatus())));
            indexSpecs.add(new IndexSpec()
                .setName("spec.vod_content")
                .setIndexFunc(
                    simpleAttribute(HandsomeMovie.class,
                        movie -> movie.getSpec().getVod_content())));
        });
    }

    @Override
    public void stop() {
        schemeManager.unregister(Scheme.buildFromType(HandsomeMovie.class));
    }
}
