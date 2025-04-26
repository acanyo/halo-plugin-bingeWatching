package cc.lik.bingeWatching.service;

import reactor.core.publisher.Mono;
import run.halo.app.core.extension.attachment.Attachment;

public interface FileAttachmentService {
    /**
     * 转存附件
     * @param picUrl 图片url
     * @return 处理后的图片URL
     */
    Mono<Attachment> updateFile(String picUrl);
}
