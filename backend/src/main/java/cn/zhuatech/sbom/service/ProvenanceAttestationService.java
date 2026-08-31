/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.sbom.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProvenanceAttestationService {
    public Assessment assess(Request request) {
        List<String> blockers = new ArrayList<>();
        List<String> actions = new ArrayList<>();
        if (!request.sbomComplete()) blockers.add("SBOM 不完整");
        if (!request.sbomSigned()) blockers.add("SBOM 未签名");
        if (!request.provenanceVerified()) blockers.add("构建来源证明未验证");
        if (!request.dependenciesPinned()) blockers.add("依赖版本或摘要未锁定");
        if (request.openCriticalVulnerabilities() > 0) blockers.add("存在未处置的严重漏洞");
        if (!request.licensePolicyPassed()) blockers.add("开源许可证策略未通过");
        if (request.waiverRequired() && !request.waiverApproved()) blockers.add("例外豁免尚未批准");
        if (!request.artifactDigestVerified()) blockers.add("发布制品摘要未验证");
        if (!blockers.isEmpty()) {
            actions.add("阻断制品发布并补齐软件供应链证据");
            return new Assessment(Decision.BLOCKED, blockers, actions);
        }
        if (!request.reproducibleBuild()) {
            actions.add("安排人工复核构建环境、工具链和不可复现差异");
            return new Assessment(Decision.REVIEW, blockers, actions);
        }
        actions.add("签发来源证明并将 SBOM、签名和制品摘要归档");
        return new Assessment(Decision.ATTEST, blockers, actions);
    }

    public record Request(@NotBlank String releaseId, boolean sbomComplete, boolean sbomSigned,
                          boolean provenanceVerified, boolean dependenciesPinned, boolean reproducibleBuild,
                          @Min(0) int openCriticalVulnerabilities, boolean licensePolicyPassed,
                          boolean waiverRequired, boolean waiverApproved, boolean artifactDigestVerified) {}
    public record Assessment(Decision decision, List<String> blockers, List<String> actions) {}
    public enum Decision { ATTEST, REVIEW, BLOCKED }
}
