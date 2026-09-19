/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.sbom.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 汇总组件漏洞、可利用性、依赖层级与许可证策略，形成供应链处置决策。
 *
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Service
public class ComponentRiskService {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Request(@NotBlank String productName, @NotBlank String componentName,
                          @NotBlank String version,
                          @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal cvss,
                          boolean exploitKnown, boolean directDependency,
                          @NotBlank String license, boolean approvedLicense, boolean fixAvailable) {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public record Result(String component, int riskScore, String severity,
                         String policyDecision, String remediationSla,
                         List<String> actions) {}

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Result evaluate(Request request) {
        int score = request.cvss().multiply(BigDecimal.TEN).intValue();
        List<String> actions = new ArrayList<>();
        if (request.exploitKnown()) { score += 20; actions.add("存在已知利用链，立即验证暴露面并临时缓解"); }
        if (request.directDependency()) score += 8;
        if (!request.approvedLicense()) { score += 25; actions.add("许可证不在允许清单，提交法务与开源治理复核"); }
        if (request.fixAvailable()) actions.add("升级到已修复版本并重新生成 SBOM 与签名证明");
        else if (request.cvss().compareTo(new BigDecimal("7.0")) >= 0) actions.add("暂无修复版本，隔离组件并持续监控上游公告");
        score = Math.min(score, 100);
        String severity = score >= 85 ? "CRITICAL" : score >= 65 ? "HIGH" : score >= 35 ? "MEDIUM" : "LOW";
        String decision = !request.approvedLicense() || score >= 85 ? "BLOCK" : score >= 50 ? "REMEDIATE" : "ALLOW";
        String sla = score >= 85 ? "24_HOURS" : score >= 65 ? "7_DAYS" : score >= 35 ? "30_DAYS" : "MONITOR";
        if (actions.isEmpty()) actions.add("保留组件来源、哈希、构建证明并按周期复扫");
        return new Result(request.componentName() + ":" + request.version(), score, severity, decision, sla, actions);
    }
}
