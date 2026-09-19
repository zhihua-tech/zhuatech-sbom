/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.sbom.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
class ProvenanceAttestationServiceTest {
    private final ProvenanceAttestationService service = new ProvenanceAttestationService();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void attestsTrustedRelease() {
        var result = service.assess(new ProvenanceAttestationService.Request("R1", true, true, true,
                true, true, 0, true, false, false, true));
        assertThat(result.decision()).isEqualTo(ProvenanceAttestationService.Decision.ATTEST);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void reviewsNonReproducibleBuild() {
        var result = service.assess(new ProvenanceAttestationService.Request("R2", true, true, true,
                true, false, 0, true, false, false, true));
        assertThat(result.decision()).isEqualTo(ProvenanceAttestationService.Decision.REVIEW);
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Test void blocksUntrustedSupplyChain() {
        var result = service.assess(new ProvenanceAttestationService.Request("R3", false, false, false,
                false, true, 2, false, true, false, false));
        assertThat(result.blockers()).hasSize(8);
    }
}
