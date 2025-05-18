package cc.lik.bingeWatching.service.impl;

import static run.halo.app.extension.index.query.QueryFactory.all;

import cc.lik.bingeWatching.MovieQuery;
import cc.lik.bingeWatching.entity.HandsomeMovie;
import cc.lik.bingeWatching.service.FileAttachmentService;
import cc.lik.bingeWatching.service.ProvideService;
import cc.lik.bingeWatching.service.SettingConfigGetter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.PageRequestImpl;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.router.selector.FieldSelector;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProvideServiceImpl implements ProvideService {
    private final ReactiveExtensionClient client;
    private final WebClient.Builder webClientBuilder;
    private final SettingConfigGetter settingConfigGetter;
    private final FileAttachmentService fileAttachmentSvc;
    
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();


    private static final List<String> REQUIRED_FIELDS = List.of(
        "vod_name", "vod_en", "vod_pic", "vod_actor", "vod_lang", "vod_year", "vod_score", "vod_content", "type_name"
    );

    @Override
    public Mono<JsonNode> getProvideMovieList(String vodName) {
        return settingConfigGetter.getBasicConfig()
            .map(config -> {
                String vod_URL = Boolean.TRUE.equals(config.getIsProxy()) ? config.getProxyHost() : "https://www.heimuer.tv";
                WebClient webClient = webClientBuilder.baseUrl(vod_URL).build();
                String MOVIE_LIST_PATH = "/api.php/provide/vod/";
                String uri = UriComponentsBuilder.fromPath(MOVIE_LIST_PATH)
                    .queryParam("ac", "videolist")
                    .queryParam("wd", vodName)
                    .build()
                    .toUriString();
                return webClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36")
                    .retrieve()
                    .bodyToMono(String.class)
                    .flatMap(responseBody -> {
                        try {
                            JsonNode jsonResponse = objectMapper.readTree(responseBody);
                            JsonNode listNode = jsonResponse.path("list");
                            ArrayNode filteredList = objectMapper.createArrayNode();
                            
                            if (listNode.isArray()) {
                                for (JsonNode movieNode : listNode) {
                                    boolean hasAnyRequiredField = false;
                                    for (String field : REQUIRED_FIELDS) {
                                        if (movieNode.has(field)) {
                                            hasAnyRequiredField = true;
                                            break;
                                        }
                                    }
                                    if (!hasAnyRequiredField) {
                                        continue;
                                    }

                                    ObjectNode filteredMovie = objectMapper.createObjectNode();
                                    REQUIRED_FIELDS.forEach(field -> {
                                        if (movieNode.has(field)) {
                                            String camelCaseField = snakeCaseToCamelCase(field);
                                            filteredMovie.set(camelCaseField, movieNode.get(field));
                                        }
                                    });
                                    filteredMovie.put("id", UUID.randomUUID().toString());
                                    filteredList.add(filteredMovie);
                                }
                            }
                            
                            // 构建成功响应体
                            ObjectNode successResponse = objectMapper.createObjectNode();
                            successResponse.put("error", false);
                            successResponse.put("message", "请求成功");
                            successResponse.put("total", filteredList.size());
                            successResponse.set("data", filteredList);
                            return Mono.just((JsonNode) successResponse);
                        } catch (Exception e) {
                            ObjectNode errorNode = objectMapper.createObjectNode();
                            errorNode.put("error", true);
                            errorNode.put("message", "获取电影列表失败: 响应内容不是有效的JSON格式 (Content-Type: text/html).");
                            errorNode.put("errorType", e.getClass().getName());
                            errorNode.put("total", 0);
                            errorNode.set("data", objectMapper.createArrayNode());
                            return Mono.just(errorNode);
                        }
                    }).onErrorResume(error -> {
                        ObjectNode errorNode = objectMapper.createObjectNode();
                        errorNode.put("error", true);
                        String message = "获取电影列表失败: " + error.getMessage();
                        if (error instanceof WebClientResponseException wcre) {
                            message = String.format("获取电影列表失败: HTTP %d. %s",
                                wcre.getStatusCode().value(), wcre.getResponseBodyAsString());
                        }
                        errorNode.put("message", message);
                        errorNode.put("errorType", error.getClass().getName());
                        errorNode.put("total", 0);
                        errorNode.set("data", objectMapper.createArrayNode());
                        return Mono.just(errorNode);
                    });
            })
            .defaultIfEmpty(Mono.error(new RuntimeException("无法获取基本配置")))
            .flatMap(mono -> mono);
    }

    private String snakeCaseToCamelCase(String snakeCase) {
        String[] parts = snakeCase.split("_");
        StringBuilder camelCase = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            camelCase.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1));
        }
        return camelCase.toString();
    }

    @Override
    public Mono<ListResult<HandsomeMovie>> listMovie(MovieQuery query) {
        return client.listBy(HandsomeMovie.class, query.toListOptions(),
            PageRequestImpl.of(query.getPage(), query.getSize(), query.getSort()));
    }

    @Override
    public Mono<Void> insertMovie(List<HandsomeMovie> movies) {
        if (movies == null || movies.isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(movies)
            .flatMap(movie -> {
                if (movie.getSpec() == null) {
                    return Mono.empty();
                }

                movie.setMetadata(new Metadata());
                movie.getMetadata().setGenerateName("handsome-movie-");

                // 处理图片
                return fileAttachmentSvc.updateFile(movie.getSpec().getVodPic())
                    .flatMap(imageUri -> {
                        movie.getSpec().setVodPic(imageUri);
                        return client.create(movie);
                    })
                    .onErrorResume(e -> {
                        log.error("Failed to process image for movie: {}", movie.getSpec().getVodName());
                        return client.create(movie);
                    });
            })
            .then();
    }
    static Sort defaultSort() {
        return Sort.by("spec.newSeen").descending();
    }
}
