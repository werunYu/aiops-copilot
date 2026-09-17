# AIOps Copilot

基于 Spring AI 的智能告警分析与根因定位助手。它把告警、监控指标、错误日志、发布记录和本地运维知识组合为可追溯的 RCA 报告。

## 当前能力

- 创建、查询和查看告警事件；状态依次为 `PENDING`、`ANALYZING`、`COMPLETED` 或 `FAILED`。
- 后台异步调用大模型与指标、日志、发布记录工具，不阻塞浏览器请求。
- 使用 SSE 实时展示分析开始、工具调用、完成或失败事件；刷新页面后仍可从数据库回放事件。
- 报告包含故障摘要、严重等级、候选根因、证据、排查步骤和本地知识引用。
- 内置 Markdown 知识库，不依赖 Qdrant；当前包含连接池、慢 SQL、Redis、GC、线程池和发布回归等场景。

## 快速启动

1. 复制环境变量模板：`Copy-Item .env.example .env`，并将 `API_KEY` 替换为 DashScope 的兼容 OpenAI API Key。
2. 启动服务：`docker compose up --build`。
3. 浏览器打开 `http://localhost:8080`。

MySQL 映射在本机 `3306`，后端映射在 `10805`。前端通过 Nginx 转发 `/api`，并已关闭 SSE 缓冲。

## 演示流程

1. 在“创建告警”中填入 `order-service`、`production` 和任意告警标题。
2. 告警内容可使用：`P99 超过 2 秒，错误率 8%，HikariPool connection timeout`。
3. 打开事件详情，点击“启动 AI 分析”。
4. 观察“分析过程”中的 SSE 时间线；完成后 RCA 报告会自动出现。

`order-service` 是当前 Mock 工具支持的服务。它提供数据库连接池紧张、连接获取超时和近期订单查询发布的固定数据，适合演示连接池耗尽或慢 SQL 的诊断链路。

## 本地开发

后端：进入 `backend/aiops` 后执行 `mvn test` 或 `mvn spring-boot:run`。

前端：进入 `frontend` 后执行 `npm install`、`npm run dev`；开发服务器会把 `/api` 转发到 `http://localhost:10805`。

## 扩展知识库

在 `backend/aiops/src/main/resources/knowledge/` 添加 Markdown 文件。每份文件的第一个一级标题会作为显示标题，正文会参与关键词检索。当前检索不使用向量数据库；将来可在 `LocalKnowledgeService` 后替换为 Qdrant 实现，而不改变报告与前端接口。

## 当前限制

- 指标、日志和发布记录仍为固定 Mock 数据，尚未接入 Prometheus、Loki/ELK 或发布平台。
- 未实现认证、权限、告警 Webhook、通知或多租户。
- 前端首期聚焦单页 RCA 工作流，尚未提供报告历史版本或人工确认闭环。
