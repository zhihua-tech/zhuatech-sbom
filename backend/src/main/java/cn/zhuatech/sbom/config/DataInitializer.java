/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.sbom.config;

import cn.zhuatech.sbom.model.*;
import cn.zhuatech.sbom.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Configuration
public class DataInitializer {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Bean
    CommandLineRunner seed(OperatingUnitRepository operatingUnits, WorkRecordRepository orders,
                           ResourceRegisterRepository resources, ReviewRecordRepository reviewRecords,
                           UserRepository users, PasswordEncoder encoder) {
        return args -> {
            if (operatingUnits.count() > 0) return;
            OperatingUnit primaryUnit = operatingUnits.save(new OperatingUnit("SBOM-APP", "应用供应链组", "研发效能中心", 180));
            OperatingUnit secondaryUnit = operatingUnits.save(new OperatingUnit("SBOM-IOT", "设备软件组", "产品工程中心", 120));
            OperatingUnit tertiaryUnit = operatingUnits.save(new OperatingUnit("SBOM-LICENSE", "开源合规组", "法务与合规部", 96));

            WorkRecord t1 = orders.save(new WorkRecord("SBOM-260801-018", "PAYMENT-API", "支付服务高危组件修复", primaryUnit, 24, 16, 1, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "REL-4.8.2"));
            WorkRecord t2 = orders.save(new WorkRecord("SBOM-260801-021", "EDGE-GATEWAY", "边缘网关固件组件盘点", secondaryUnit, 18, 8, 0, LocalDate.now().plusDays(1), WorkRecord.Status.RUNNING, "FW-12.6"));
            WorkRecord t3 = orders.save(new WorkRecord("SBOM-260802-006", "DATA-CONSOLE", "数据控制台许可证复核", tertiaryUnit, 12, 0, 0, LocalDate.now().plusDays(3), WorkRecord.Status.RELEASED, "REL-3.2"));
            WorkRecord t4 = orders.save(new WorkRecord("SBOM-260728-015", "MOBILE-CLIENT", "移动客户端发布物证明归档", primaryUnit, 20, 20, 1, LocalDate.now(), WorkRecord.Status.COMPLETED, "IOS-8.9"));

            resources.saveAll(List.of(
                new ResourceRegister("SCAN-SCA-03", "应用依赖扫描器", primaryUnit, ResourceRegister.Status.RUNNING, 96),
                new ResourceRegister("SCAN-FW-02", "固件组件识别器", secondaryUnit, ResourceRegister.Status.IDLE, 79),
                new ResourceRegister("POL-LIC-05", "许可证策略引擎", tertiaryUnit, ResourceRegister.Status.RUNNING, 92),
                new ResourceRegister("FEED-VULN-08", "漏洞情报同步", primaryUnit, ResourceRegister.Status.ALARM, 66)
            ));
            reviewRecords.saveAll(List.of(
                new ReviewRecord("CMP-260801-032", t1, "高危漏洞可利用性确认", 6, 0, ReviewRecord.Result.PASSED, "何谨"),
                new ReviewRecord("CMP-260801-011", t2, "固件组件来源核验", 3, 0, ReviewRecord.Result.PASSED, "陆遥"),
                new ReviewRecord("CMP-260801-018", t4, "构建证明完整性复核", 5, 1, ReviewRecord.Result.FAILED, "何谨"),
                new ReviewRecord("CMP-260802-003", t3, "许可证兼容性评审", 4, 0, ReviewRecord.Result.PENDING, "陆遥")
            ));
            String demo = encoder.encode("Demo@2026");
            users.saveAll(List.of(
                new UserAccount("operator", demo, "陆遥", UserAccount.Role.DOMAIN_USER, "SBOM-APP"),
                new UserAccount("planner", demo, "何谨", UserAccount.Role.DOMAIN_OPERATOR, null),
                new UserAccount("quality", demo, "顾清", UserAccount.Role.QUALITY, null),
                new UserAccount("admin", encoder.encode("ZhuaTech@2026"), "系统管理员", UserAccount.Role.ADMIN, null)
            ));
        };
    }
}
