/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.sbom.controller;

import cn.zhuatech.sbom.common.ApiResponse;
import cn.zhuatech.sbom.service.ProvenanceAttestationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/enterprise/sbom")
public class ProvenanceAttestationController {
    private final ProvenanceAttestationService service;
    public ProvenanceAttestationController(ProvenanceAttestationService service) { this.service = service; }
    @PostMapping("/provenance-attestation")
    public ApiResponse<ProvenanceAttestationService.Assessment> assess(
            @Valid @RequestBody ProvenanceAttestationService.Request request) {
        return ApiResponse.ok(service.assess(request));
    }
}
