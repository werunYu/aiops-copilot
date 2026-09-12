# AIOps Copilot 系统架构设计

## 1. 设计目标

AIOps Copilot 是一个基于 Spring AI Alibaba 的智能故障分析助手。

系统通过大模型 Agent 自动理解系统告警，并根据告警上下文自主调用监控指标、日志查询、部署记录等工具，同时结合本地运维知识库，生成带有证据链和排查建议的 RCA 报告。

本项目的架构设计目标：

1. 支持本地一键运行
2. 体现 Agent 自主决策能力
3. 体现 Tool Calling 能力
4. 体现 RAG 知识检索能力
5. 支持结构化 RCA 输出
6. 保持实现简单，保证一周内完成 MVP
7. 便于后续接入真实监控和日志系统

---

## 2. 总体架构

系统采用前后端分离架构，后端采用模块化单体设计。

```text
                         Browser
                            |
                            v
                  Vue 3 + Element Plus
                            |
                         REST API
                            |
                            v
                 Spring Boot Backend
                            |
          +-----------------+-----------------+
          |                 |                 |
          v                 v                 v
   Incident Module    Agent Module      Report Module
                            |
                            v
                    Spring AI Alibaba
                            |
                            v
                      ReactAgent
                            |
          +-----------------+-----------------+
          |                 |                 |
          v                 v                 v
     Metrics Tool      Log Tool       Deployment Tool
          |                 |                 |
          v                 v                 v
      Mock Data         Mock Data        Mock Data

                            |
                            v
                     Knowledge Search
                            |
                            v
                    Qdrant Vector Store
                            |
                            v
                    DashScope / LLM