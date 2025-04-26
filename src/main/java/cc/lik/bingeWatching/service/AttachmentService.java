package cc.lik.bingeWatching.service;

import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import reactor.core.publisher.Mono;

public interface AttachmentService {
    /**
     * 转存附件
     * @param picUrl 图片url
     * @param groupName 分组名
     * @param policyName 存储策略名
     * @return halo附件库
     */
    Mono<Attachment> updateFile(String picUrl,String groupName,String policyName);
}
