package cc.lik.bingeWatching.service;

import reactor.core.publisher.Mono;

/**
 * 文件附件服务
 */
public interface FileAttachmentService {
    /**
     * 更新文件并返回新的URL
     * @param picUrl 原始图片URL
     * @return 处理后的图片URL
     */
    Mono<String> updateFile(String picUrl);
}
