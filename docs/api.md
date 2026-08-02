# SBOM API 摘要

版权所有 © 2026 上海如静知华信息科技有限公司。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/login` | 登录并获取 JWT |
| GET | `/api/admin/dashboard` | 软件供应链风险指标 |
| GET | `/api/admin/work-orders` | 组件治理任务 |
| GET | `/api/shopfloor/dashboard` | 组件工程师工作台 |
| POST | `/api/shopfloor/work-orders/{id}/reports` | 提交升级、缓解和证明 |
| POST | `/api/shopfloor/component-risk` | 合并漏洞、利用、依赖和许可证策略评估组件 |

正式实现建议使用 PURL 标识组件，并在此接口前完成漏洞情报、许可证清单和产品暴露面的标准化。
