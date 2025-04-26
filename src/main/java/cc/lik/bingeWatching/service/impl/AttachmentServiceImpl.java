package cc.lik.bingeWatching.service.impl;

import cc.lik.bingeWatching.service.AttachmentService;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.Attachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    @Override
    public Mono<Attachment> updateFile(String picUrl, String groupName, String policyName) {
        return null;
    }
}
