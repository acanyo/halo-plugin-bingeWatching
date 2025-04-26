package cc.lik.bingeWatching.service.impl;

import cc.lik.bingeWatching.service.FileAttachmentService;
import cc.lik.bingeWatching.service.SettingConfigGetter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import run.halo.app.core.extension.service.AttachmentService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileFileAttachmentServiceImpl implements FileAttachmentService {
    private final SettingConfigGetter settingConfigGetter;
    private final AttachmentService attachmentSvc;
    private final DataBufferFactory dataBufferFactory = DefaultDataBufferFactory.sharedInstance;

    @Override
    public Mono<String> updateFile(String picUrl) {
        return settingConfigGetter.getBasicConfig()
            .switchIfEmpty(Mono.error(new RuntimeException("无法获取基本配置")))
            .flatMap(config -> {
                // 检查是否需要转存
                if (!Boolean.TRUE.equals(config.getEnablePicDump())) {
                    log.info("图片转存功能未启用");
                    return Mono.just(picUrl);
                }
                if (!isImageUrl(picUrl)) {
                    log.info("不是有效的图片URL: {}", picUrl);
                    return Mono.just(picUrl);
                }
                HttpClient httpClient = HttpClient.create()
                    .responseTimeout(Duration.ofSeconds(30))
                    .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
                WebClient webClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .build();
                final String originalFileName = getFileName(picUrl);
                final MediaType originalMediaType = getMediaType(originalFileName);
                AtomicReference<String> fileNameRef = new AtomicReference<>(originalFileName);
                AtomicReference<MediaType> mediaTypeRef = new AtomicReference<>(originalMediaType);
                return downloadImage(webClient, picUrl)
                    .map(imageBytes -> {
                        // 设置文件名和媒体类型
                        fileNameRef.set(originalFileName);
                        mediaTypeRef.set(originalMediaType);
                        return imageBytes;
                    })
                    .flatMap(finalImageBytes -> {
                        Flux<DataBuffer> dataBufferFlux = Flux.just(dataBufferFactory.wrap(finalImageBytes));
                        return attachmentSvc.upload(
                            config.getFilePolicy(),
                            config.getFileGroup(),
                            fileNameRef.get(),
                            dataBufferFlux,
                            mediaTypeRef.get()
                        );
                    })
                    .map(attachment -> 
                        attachment.getMetadata().getAnnotations().get("storage.halo.run/uri")
                    )
                    .onErrorResume(e -> {
                        log.error("图片处理失败，使用原始URL: {}, 错误: {}", picUrl, e.getMessage());
                        return Mono.just(picUrl);
                    });
            });
    }

    /**
     * 下载图片
     */
    private Mono<byte[]> downloadImage(WebClient webClient, String url) {
        return webClient.get()
            .uri(url)
            .retrieve()
            .bodyToMono(byte[].class)
            .onErrorResume(e -> {
                log.error("下载图片失败: {}", e.getMessage());
                return Mono.error(e);
            });
    }

    /**
     * 获取文件的 MediaType
     */
    private MediaType getMediaType(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (lowerFileName.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (lowerFileName.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        } else if (lowerFileName.endsWith(".webp")) {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    /**
     * 检查是否是有效的图片 URL
     */
    private boolean isImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String lowerUrl = url.toLowerCase();
        return lowerUrl.endsWith(".jpg") ||
               lowerUrl.endsWith(".jpeg") ||
               lowerUrl.endsWith(".png") ||
               lowerUrl.endsWith(".gif") ||
               lowerUrl.endsWith(".webp");
    }

    /**
     * 从 URL 中获取文件名
     */
    private String getFileName(String url) {
        if (url == null || url.isEmpty()) {
            return "unknown";
        }
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
