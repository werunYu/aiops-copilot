## 7 天开发计划

### Day 1：需求与设计

- 安装 ChatGPT；
- 编写 PRD；
- 编写技术架构；
- 设计 Agent、Tool、数据库和 API；
- 生成开发计划。

产出：

```text
docs/requirements.md
docs/architecture.md
docs/development-plan.md
```

### Day 2：基础工程

- 创建 Spring Boot 项目；
- 创建数据库表；
- 实现 Incident CRUD；
- 创建前端页面；
- 准备 Mock 数据；
- 配置 Docker。

验收：项目能启动，能创建和查看告警。

### Day 3：接入大模型

- 接入 ChatModel；
- 编写告警分析 Prompt；
- 实现结构化输出；
- 实现 RCA DTO；
- 完成分析接口。

验收：输入告警后返回 RCA JSON。

### Day 4：Agent 与 Tools

- 实现 ReactAgent；
- 实现 MetricsTool；
- 实现 LogQueryTool；
- 实现 DeploymentTool；
- 记录 Agent 执行事件。

验收：Agent 可以自主调用工具，页面可以看到调用过程。

### Day 5：RAG

- 编写 8～12 篇知识文档；
- 实现文档导入；
- 配置 Embedding；
- 接入 Qdrant；
- 将检索结果注入 Agent。

验收：Agent 能结合知识库完成分析。

### Day 6：工程化与测试

- 增加异常处理；
- 增加超时处理；
- 增加日志；
- 编写单元测试和集成测试；
- 完善 Docker Compose；
- 完善 README；
- 检查配置脱敏。

### Day 7：演示与交付

- 优化 UI；
- 准备固定演示数据；
- 完成完整 Demo；
- 录制演示视频；
- 绘制架构图；
- 整理 CC 和 Superpowers 使用记录；
- 完成最终验收。