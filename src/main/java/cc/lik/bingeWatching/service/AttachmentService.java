package cc.lik.bingeWatching.service;

import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import reactor.core.publisher.Mono;
import java.io.File;

public interface AttachmentService {
    /**
     * 转存附件
     * @param file 附件文件
     * @param groupName 分组名
     * @param policyName 存储策略名
     * @return halo附件库
     */
    Mono<Attachment> updateFile(File file,String groupName,String policyName);
}
