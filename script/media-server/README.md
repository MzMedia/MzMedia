# MzMedia 流媒体服务器测试环境 (ZLM & ABL)

本项目为 `yudao-module-media`（视频监控与流媒体中心）提供统一的流媒体服务器 Docker 镜像构建、部署与测试套件，全面支持 **ZLMediaKit (ZLM)** 与 **ABLMediaServer (ABL)** 双流媒体引擎。

---

## 📁 目录结构

```text
script/media-server
├── docker-compose.yml       # 统一编排文件 (支持一键启动 ZLM / ABL / 全部)
├── .env.example             # 端口、密钥、IP等环境变量示例
├── .env                     # 环境变量配置文件
├── start.sh                 # Linux / macOS 一键启动脚本
├── start.bat                # Windows 交互式一键启动脚本
├── README.md                # 部署与测试指南
├── zlm/                     # ZLMediaKit 模块
│   ├── config.ini           # ZLM 预调优配置文件 (预设 REST Secret 与 Webhook)
│   ├── Dockerfile           # ZLM 自定义镜像 Dockerfile
│   ├── run-zlm.sh           # 单容器快速启动脚本 (Linux/macOS)
│   └── run-zlm.bat          # 单容器快速启动脚本 (Windows)
├── abl/                     # ABLMediaServer 模块
│   ├── Dockerfile           # ABL 镜像构建文件 (支持自动下载或离线构建)
│   ├── ABLMediaServer.ini   # ABL 预调优配置文件 (预设 REST Secret 与 Webhook)
│   ├── entrypoint.sh        # ABL 容器入口脚本 (动态环境参数注入与信号处理)
│   ├── build-abl.sh         # ABL 镜像构建脚本 (Linux/macOS)
│   ├── build-abl.bat        # ABL 镜像构建脚本 (Windows)
│   ├── run-abl.sh           # 单容器快速启动脚本 (Linux/macOS)
│   └── run-abl.bat          # 单容器快速启动脚本 (Windows)
└── test/                    # 自动化测试验证工具
    ├── test-api.sh          # REST API 连通性探测 (Linux/macOS)
    ├── test-api.ps1         # REST API 连通性探测 (PowerShell)
    ├── push-test-stream.sh  # FFmpeg 模拟推流测试 (Linux/macOS)
    └── push-test-stream.ps1 # FFmpeg 模拟推流测试 (PowerShell)
```

---

## ⚡ 快速启动

### 方式一：使用一键启动脚本 (推荐)

- **Windows 系统**：双击运行 `start.bat`，即可通过交互式菜单按需启动 ZLM、ABL 或执行测试。
- **Linux / macOS 系统**：
  ```bash
  cd script/media-server
  chmod +x *.sh */*.sh test/*.sh
  
  # 启动全部流媒体服务 (ZLM + ABL)
  ./start.sh all
  
  # 或者仅启动 ZLM
  ./start.sh zlm
  
  # 或者仅启动 ABL
  ./start.sh abl
  ```

### 方式二：使用 Docker Compose 命令

```bash
# 启动所有流媒体服务
docker compose up -d

# 仅启动 ZLMediaKit
docker compose up -d zlm

# 仅构建并启动 ABLMediaServer
docker compose up -d --build abl

# 停止所有服务
docker compose down
```

---

## 🔌 端口映射与协议矩阵

为了确保在同一台宿主机测试时 **ZLM** 与 **ABL** 互不干扰，默认端口已预先规划好隔离区间：

| 服务组件 | 协议类型 | 容器内部端口 | 宿主机映射端口 | 默认说明 |
| :--- | :--- | :--- | :--- | :--- |
| **ZLMediaKit (ZLM)** | HTTP REST API / FLV | `80` | `18080` | 管理接口与 HTTP-FLV 播放 |
| | HTTPS API / FLV | `443` | `18443` | SSL 加密访问 |
| | RTSP | `554` | `554` | RTSP 推拉流 |
| | RTMP | `1935` | `1935` | RTMP 推拉流 |
| | WebRTC | `8000/udp` | `8000/udp` | 毫秒级极速低延迟播放 |
| | RTP 收流 (单端口) | `10000` | `10000` (TCP/UDP) | 国标 GB28181 接收 RTP 流 |
| | RTP 收流 (多端口) | `30000-30050/udp` | `30000-30050/udp` | 国标动态端口接收 |
| **ABLMediaServer (ABL)**| HTTP REST API | `9000` | `9000` | 管理接口 |
| | HTTP-FLV | `9001` | `9001` | FLV 视频播放端口 |
| | WS-FLV | `9002` | `9002` | WebSocket-FLV 播放端口 |
| | HTTP-MP4 / HLS | `9003` / `9004` | `9003` / `9004` | MP4 下载与 HLS 播放 |
| | RTSP | `5544` | `5544` | 避开 554，避免与 ZLM 冲突 |
| | RTMP | `1936` | `1936` | 避开 1935，避免与 ZLM 冲突 |
| | RTP PS/TS 收流 | `10002` | `10002` (TCP/UDP) | 国标 RTP 接收端口 |
| | JT/T 1078 收流 | `7078` | `7078` (TCP) | 部标车载音视频推流端口 |
| | WebRTC | `9005/udp` | `9005/udp` | ABL WebRTC 播放 |
| | WebRTC RTP 范围 | `31000-31050/udp` | `31000-31050/udp` | 避开 ZLM 30000 范围 |

> 提示：如果需要调整端口或将 ZLM HTTP 改为 80，直接修改 `script/media-server/.env` 文件即可。

---

## 🛠️ 后端 `mz-media` 对接配置

在 `yudao-server/src/main/resources/application.yaml` 或运行环境变量中配置流媒体节点：

### 1. 对接 ZLMediaKit (默认)

```yaml
media:
  id: default
  type: zlm                     # 指定类型为 zlm
  ip: 127.0.0.1                 # 流媒体服务IP
  http-port: 18080              # 对应 ZLM_HTTP_PORT
  secret: 035c73f7-bb6b-4889-a715-d9eb2d1925cc
  rtp:
    enable: true
    port-range: 30000,30500
    send-port-range: 30501,31000
```

### 2. 对接 ABLMediaServer

```yaml
media:
  id: abl_default
  type: abl                     # 指定类型为 abl
  ip: 127.0.0.1                 # 流媒体服务IP
  http-port: 9000               # 对应 ABL_HTTP_PORT
  secret: 035c73f7-bb6b-4889-a715-d9eb2d1925cc
  rtp:
    enable: false               # ABL 默认支持单端口 PS/TS 收流 (10002)
    port-range: 30000,30500
```

### 3. Webhook 回调说明

两个流媒体服务器已配置好向 `host.docker.internal:48080` 发起 Webhook 回调：
- **ZLM 回调路径**：`/index/hook/...`
- **ABL 回调路径**：`/index/hook/abl/...`

---

## 🧪 测试与验证

### 1. REST API 自动化联通性测试

执行测试脚本，自动校验服务健康状态、鉴权密钥及流列表：
- **Windows (PowerShell)**:
  ```powershell
  .\test\test-api.ps1
  ```
- **Linux / macOS**:
  ```bash
  ./test/test-api.sh
  ```

### 2. 模拟摄像头推流测试 (无须实体摄像头)

脚本将自动通过本机 FFmpeg（若未安装则自动拉取 Docker FFmpeg 容器）生成彩条与音频测试流推送到服务器：

- **推流到 ZLM**:
  ```powershell
  # Windows
  .\test\push-test-stream.ps1 -Target zlm
  
  # Linux
  ./test/push-test-stream.sh zlm
  ```
  播放地址：`http://127.0.0.1:18080/live/test.live.flv` 或 `rtsp://127.0.0.1:554/live/test`

- **推流到 ABL**:
  ```powershell
  # Windows
  .\test\push-test-stream.ps1 -Target abl
  
  # Linux
  ./test/push-test-stream.sh abl
  ```
  播放地址：`http://127.0.0.1:9001/live/test.flv` 或 `ws://127.0.0.1:9002/live/test.flv`

---

## ❓ 常见问题排查 (FAQ)

1. **容器启动报错端口冲突？**
   - 检查本机是否有其他服务占用了 `554`、`1935` 等端口（例如本机已运行了其他流媒体服务器或 IIS）。在 `.env` 中修改对应的 `ZLM_RTSP_PORT` 或 `ZLM_HTTP_PORT` 即可。

2. **流媒体服务器无法回调后端 Webhook？**
   - 确认后端服务端口是否为 `48080`；
   - 在 Windows/macOS Docker Desktop 下，容器访问宿主机通过 `host.docker.internal` 解析；Linux 环境下如果在同一 Docker 网络，可直接使用容器名通信。

3. **ABL 首次构建由于 GitHub 网络较慢超时？**
   - 可提前下载官方 Release 的 `Linux-webrtc.zip` 放置在 `script/media-server/abl/` 目录下，脚本检测到本地文件后会自动执行离线免下载构建。
