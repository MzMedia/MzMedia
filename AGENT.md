# MzMedia 项目 Git 协作与开发规范

本文档为 MzMedia（基于 `ruoyi-vue-pro` 的视频联动物联网平台）的 Git 协作、代码提交以及与官方上游仓库同步的标准规范。**所有开发者及 AI Agent 在进行 Git 操作、功能开发和代码合并时，必须严格遵守本规范。**

---

## 一、 远程仓库与分支架构

### 1.1 远程仓库定义 (Remotes)

| 远程源名称 | 仓库地址 | 权限与职责 |
| :--- | :--- | :--- |
| **`origin`** | `https://gitee.com/mzmedia/mz-media.git` | **团队自有仓库**。拥有读写权限，日常开发代码、备份分支均推送到此。 |
| **`upstream`** | `https://gitee.com/zhijiantianya/ruoyi-vue-pro.git` | **官方上游仓库**。只读源，仅用于拉取官方最新代码与修复。 |

检查 Remote 配置命令：
```bash
git remote -v
```

---

### 1.2 分支模型与职责

```text
[upstream/master-jdk25] (官方上游)
          │  git pull --ff-only upstream master-jdk25
          ▼
   official-jdk25      (本地纯净镜像，严禁直接提交业务代码)
          │  git merge official-jdk25
          ▼
        dev            (★ 团队开发主基线) ──push──> [origin/dev]
          │
          ├── feature/xxx  (具体功能开发分支)
          └── hotfix/xxx   (紧急 Bug 修复分支)
```

1. **`official-jdk25`（官方纯净镜像分支）**：
   - **唯一职责**：严格跟踪 `upstream/master-jdk25`。
   - **绝对红线**：**严禁直接在此分支上修改任何文件或做任何 commit**。它必须保持与官方上游完全干净、纯正的 Git 历史。
2. **`dev`（团队业务开发主基线）**：
   - 所有定制化模块（如 `yudao-module-media`、`ui` 前端）以及日常开发成果的主分支。
   - 与 `official-jdk25` 保持直接祖先关联，用于接收上游合并。
3. **`feature/*`（功能特性分支）**：
   - 基于 `dev` 切出，如 `feature/ptz-control`、`feature/device-gb28181`。开发测试完成后通过 PR 或 Merge 回合并到 `dev`。
4. **`backup-*`（历史安全备份分支）**：
   - 重大架构调整或重构前建立的快照备份，如 `backup-old-history`。

---

## 二、 提交规范 (Commit Standards)

遵循 **Conventional Commits**（约定式提交）标准，提交信息格式统一为：

```text
<type>(<scope>): <subject>
```

### 2.1 Type 说明

| Type | 说明 | 示例 |
| :--- | :--- | :--- |
| **`feat`** | 新增业务功能 | `feat(media): 支持国标设备云台 PTZ 预置位控制` |
| **`fix`** | 修复 Bug | `fix(sip): 修复 TCP 模式下分包粘包解析异常` |
| **`refactor`** | 代码重构（无新功能，也非修复 Bug） | `refactor(media): 优化 ZLMediaKit Webhook 事件分发逻辑` |
| **`perf`** | 性能优化 | `perf(iot): 优化 Modbus 点位采集并发调度` |
| **`docs`** | 文档变更 | `docs(readme): 补充媒体服务环境部署说明` |
| **`style`** | 代码格式修改（空格、缩进，不影响运行） | `style(ui): 规范前端视频播放器组件样式` |
| **`chore`** | 构建/依赖/配置变动/同步上游 | `chore: 同步 upstream/master-jdk25 官方更新` |

### 2.2 Scope 建议范围

- `media`：视频监控/WVP/ZLMediaKit/GB28181 相关
- `iot`：物联网设备、协议与规则引擎
- `system`：用户、权限、字典等系统服务
- `infra`：代码生成、文件存储、配置中心等
- `ui`：前端界面与播放器组件
- `server`：Server 引导与核心配置
- `deps`：Maven / npm 依赖变更

### 2.3 提交准则

- **原子性提交**：一个 Commit 只解决一个问题或完成一个功能点，禁止把数天的混合修改打包成一个模糊的大提交。
- **禁止提交内容**：
  - 本地缓存与环境文件（如 `.idea/`、`target/`、`node_modules/`、`*.log`）。
  - 含有生产真实密码或敏感 Key 的 `application-*.yaml`。

---

## 三、 同步上游官方更新规范 (Upstream Sync Workflow)

当官方 `zhijiantianya/ruoyi-vue-pro` 的 `master-jdk25` 分支有安全修复或新功能时，按照以下**标准化 4 步流程**进行同步：

### 步骤 1：确认工作区状态干净
在开始同步前，确保当前工作分支没有未提交的脏代码：
```bash
git status
# 如果有未完成的修改，先暂存
git stash
```

### 步骤 2：更新本地纯净基准分支 (`official-jdk25`)
```bash
git checkout official-jdk25
git pull --ff-only upstream master-jdk25
```
> [!NOTE]
> 使用 `--ff-only` 可以确保该分支只有快进更新，一旦报错说明本地纯净分支被误改过，需立即排查。

### 步骤 3：将更新合并到业务开发分支 (`dev`)
```bash
git checkout dev
git merge official-jdk25
```

### 步骤 4：处理合并与冲突验证
1. **重点核查文件**（防止官方覆盖本地定制）：
   - 根目录 `pom.xml`（确保 `<java.version>21</java.version>`、`yudao-module-media` 未被冲掉）
   - `yudao-dependencies/pom.xml`（确保 GB28181 与视频依赖完整）
   - `yudao-server/src/main/resources/application.yaml`（确保 `sip:`、`media:`、`user-settings:` 未丢失）
2. **解决冲突并编译验证**：
   - 若有冲突文件，解决后执行 `git add <file>`。
   - 本地运行 Maven 编译或前端检查以确保语法通过。
3. **完成提交并推送备份**：
   ```bash
   git commit -m "chore: 同步 upstream/master-jdk25 官方更新"
   
   # 推送开发分支与官方基准分支到团队仓库
   git push origin dev
   git push origin official-jdk25
   ```
4. 如果步骤 1 中执行了 stash，此时弹出暂存代码：`git stash pop`。

---

## 四、 减少合并冲突的架构开发防线

为了保证未来每次与上游同步时**冲突最小化**，开发时应遵守以下原则：

1. **业务逻辑独立模块化**：
   - 视频相关的全部 Controller、Service、DAO 及 SIP 信令代码**必须**放在 `yudao-module-media` 中。
   - **禁止**直接修改 `yudao-framework` 或 `yudao-module-system` 的内部核心逻辑。若需扩展框架能力，应采用 Spring 继承拓展、`@Primary` Bean 覆盖或 AOP 切面实现。
2. **配置文件隔离**：
   - 避免直接在公共 `application.yaml` 中大规模修改官方现有参数；新增的视频监控专属配置统一挂载在 `sip:`、`media:`、`user-settings:` 等专属命名空间下。
3. **数据库脚本管理**：
   - 视频业务表及数据统一放置在 `sql/module/media-xxxx-xx-xx.sql`。
   - 严禁直接改动官方历史 SQL（如 `ruoyi-vue-pro.sql`），增量变更以独立 SQL 文件版本递增。

---

## 五、 AI Agent 行为约束规范

凡参与本项目代码开发的 AI Agent，在涉及 Git 操作时必须遵循：
1. **禁止越界提交**：严禁向 `official-jdk25` 分支提交任何业务代码；
2. **状态先验**：执行分支切换与合并前，必须先行调用 `git status` 确保工作树状态；
3. **安全第一**：在执行变基（Rebase）、强制推送（Force Push）或删除分支等高危操作前，必须向用户明确确认，或先建立备份分支。
