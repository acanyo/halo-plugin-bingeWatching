package cc.lik.bingeWatching;

import java.util.Properties;
import lombok.RequiredArgsConstructor;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.theme.dialect.TemplateHeadProcessor;

@Component
@RequiredArgsConstructor
public class MovieHeadProcessor implements TemplateHeadProcessor {

    static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER =
        new PropertyPlaceholderHelper("${", "}");

    private final PluginWrapper pluginWrapper;

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
        IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        
        Properties properties = new Properties();
        properties.setProperty("version", pluginWrapper.getDescriptor().getVersion());

        String script = PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders("""
            <!-- bingeWatching start -->
            <script type="text/javascript" src="/plugins/bingeWatching/assets/static/js/movie-wall.js?version=${version}"></script>
            <script type="text/javascript" src="/plugins/bingeWatching/assets/static/js/movie-detail.js?version=${version}"></script>
            <!-- bingeWatching end -->
            """, properties);

        model.add(modelFactory.createText(script));
        return Mono.empty();
    }
}
