# MzMedia - Spring AI 驱动的智能中枢与 MCP Agent (`yudao-module-ai1-springai`)

## 📌 模块定位与职责

`yudao-module-ai1-springai` 是 MzMedia 专为 AIoT 物联网与视频融合场景量身打造的**下一代 AI 智能体（Agent）开发套件**。

不同于市面上常见的单一 Prompt 聊天套壳，本模块深度融合 **Spring AI 1.1+** 与 **Anthropic 官方 Model Context Protocol (MCP)** 标准，实现了：
1. **统一模型接入**：支持对接 OpenAI、DeepSeek、Claude、Gemini、通义千问、MiniMax 等 18+ 主流大模型厂商；
2. **MCP 工具标准化集成**：通过官方 Java MCP SDK (`io.modelcontextprotocol.client.McpSyncClient`)，同时支持本地 `stdio`（子进程）与远程 `http`（Streamable HTTP）双传输通道；
3. **AIoT 时序运维诊断智能体**：打通 TDengine 时序数据库与企业知识库（RAG），工程师可用自然语言排查设备故障原因并生成维保建议；
4. **私有非标协议智能逆向解析**：输入十六进制报文或协议 PDF，自动推理帧结构与校验算法，一键生成符合规范的 Java Netty 编解码器代码与单元测试；
5. **动态 Skill 沙箱体系**：支持用户在后台在线维护多层级代码/脚本文件树，并在隔离沙箱中动态物化注入供智能体执行。

---

## 🏗️ 核心架构与交互链路

```text
 ┌────────────────────────────────────────────────────────┐
 │           用户自然语言运维交互 / 移动端语音指令        │
 └───────────────────────────┬────────────────────────────┘
                             │ SSE 流式 Token (WebFlux)
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │          AI Agent 智能编排中枢 (Ai1AgentService)       │
 │   · 角色设定 (System Prompt)                           │
 │   · 会话短期记忆 (Session) + 事实长期记忆 (Memory)     │
 │   · 知识库检索增强 (RAG Vector Store)                  │
 └─────────────┬────────────────────────────▲─────────────┘
               │ Tool Call / Function       │ 诊断结论
               ▼                            │
 ┌──────────────────────────────────────────┴─────────────┐
 │       Model Context Protocol (MCP) 客户端管理器        │
 │  ┌───────────────────────┐   ┌──────────────────────┐  │
 │  │ stdio Transport       │   │ http Transport       │  │
 │  │ (本地 Python/Node 工具)│   │ (远程 Streamable MCP)│  │
 │  └───────────┬───────────┘   └──────────┬───────────┘  │
 └──────────────┼──────────────────────────┼──────────────┘
                ▼                          ▼
 ┌────────────────────────────────────────────────────────┐
 │                   MzMedia 底座能力暴露                 │
 │ · 查询 TDengine 历史时序数据 (电流/温度/振动)          │
 │ · 获取设备告警流水与历史维修工单                       │
 │ · 触发 GB28181 摄像头预置位转动并截取关键帧            │
 │ · 动态加载 Skill 脚本沙箱 (Ai1SkillToolFactory)        │
 └────────────────────────────────────────────────────────┘
```

---

## 📂 核心包目录结构

```text
cn.iocoder.yudao.module.ai1
├── controller         # REST Controller 接口层 (Agent 编排, 模型配置, MCP 工具, 会话交互)
├── dal                # 数据访问层 (dataobject 实体 与 mysql Mapper)
├── enums              # AI 领域枚举 (McpTransportType, AgentType, ModelType 等)
├── framework          # Spring Boot 自动装配、动态 LLM Client 工厂 (Ai1LlmModelFactory)
├── harness            # ★ 核心引擎驱动层 (LLM 调用、MCP 抽象、Skill 物化沙箱、RAG 向量检索)
├── job                # 后台定时调度任务 (长期记忆归档、知识库向量化批处理)
├── service            # 业务服务层
│   ├── agent          # 智能体全生命周期管理
│   ├── home           # 运营首页与用量大屏统计
│   ├── knowledge      # RAG 知识库与文档切片向量化
│   ├── mcp            # MCP 服务端管理与动态 Tool 提取
│   ├── memory         # 长期记忆与上下文窗口裁剪
│   ├── model          # 大模型提供商与模型元数据
│   ├── session        # 聊天会话历史与流式消息投递
│   └── skill          # 技能代码树与沙箱隔离执行
└── util               # 报文逆向解析工具、Prompt 模版引擎
```

---

## 💾 核心数据库表结构 (`src/test/resources/sql/create_tables.sql`)

| 表名 | 业务定位 | 核心说明 |
| :--- | :--- | :--- |
| **`ai1_provider`** | 模型提供商表 | 配置 BaseUrl、API Key 占位符、供应商类型（OpenAI, DeepSeek, Claude, Gemini 等）。 |
| **`ai1_model`** | 模型元数据表 | 登记具体模型标识（如 `deepseek-chat`、`gpt-4o`）、上下文窗口尺寸、计费倍率。 |
| **`ai1_mcp`** | MCP 客户端注册表 | 记录传输类型（`stdio` / `http`）、服务启动命令/URL、动态 Tools 缓存与配置指纹。 |
| **`ai1_agent`** | 智能体配置表 | 绑定的基础模型、System Prompt、关联的 MCP 服务清单、挂载的知识库与 Skill。 |
| **`ai1_knowledge_base`** | RAG 知识库主表 | 向量检索配置、相似度阈值、召回 Top-K、向量模型绑定。 |
| **`ai1_knowledge_document`**| 知识文档表 | 文档切片配置（ChunkSize、ChunkOverlap）、解析状态与切片统计。 |
| **`ai1_skill` / `ai1_skill_file`**| 技能代码树 | 存储技能多层级脚本文件树，供 `Ai1SkillToolFactory` 运行时物化进独立沙箱。 |
| **`ai1_session` / `ai1_session_message`**| 会话与消息流水 | 记录单次问答与多轮对话历史，支持流式 SSE Token 输出持久化。 |
| **`ai1_memory`** | 事实性长期记忆表 | 提炼用户偏好、设备历史故障事实与企业实体关系。 |

---

## 🤖 核心落地业务场景

### 1. 私有非标工业协议智能逆向解析
- **解决痛点**：中小企业常有定制仪表或非标工控机，缺乏驱动代码，手写 Netty 编解码器繁琐耗时。
- **运作机制**：
  1. 工程师在前端上传十六进制报文（如 `01 03 00 00 00 02 C4 0B`）或协议 PDF 文档；
  2. AI 智能体自动解析报文头、从站地址、功能码、寄存器地址、长度域及 CRC 校验算法；
  3. 自动生成符合 `yudao-module-iot-gateway` 标准规范的 Java 解码器类与单元测试代码，一键复制或动态装载。

### 2. AIoT 时序运维诊断智能体
- **解决痛点**：设备故障排查全凭老技工经验，时序数据分散，难以及时定位诱因。
- **运作机制**：
  - 运维人员提问：“*2 号车间挤出机昨天下午为何突发报警？*”
  - AI Agent 自动调用 MCP Tool 查询 TDengine 对应时间段的电流、温升时序曲线；
  - 调取系统告警日志与知识库《挤出机常见故障处理规程》；
  - 自动输出归因分析报告，并提示：“建议检查 3 号加热圈阻值，并指派维保工单”。

---

## ⚙️ 常见配置项说明 (`application.yaml`)

```yaml
yudao:
  ai:
    client:
      enabled: true                      # 启用自定义动态 LLM 工厂
    mcp:
      timeout: 30                        # MCP 进程启动与调用超时 (秒)
      work-dir: ${user.home}/mzmedia/mcp # stdio 本地进程工作目录

spring:
  ai:
    chat:
      client:
        enabled: false                   # 禁用 Spring 默认单一单例注入，避免与动态工厂冲突
```
