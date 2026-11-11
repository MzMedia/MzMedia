# MzMedia - 视频监控与流媒体中心 (`yudao-module-media`)

## 📌 模块定位与职责

`yudao-module-media` 是 MzMedia 平台的核心子系统之一，深度整合 **国标 GB/T 28181（2016/2022）协议栈** 与 **ZLMediaKit 高性能流媒体服务器集群**。该模块专为中小企业工业安防、车间巡检、园区安保及环保监测场景设计，提供**视频设备一键接入、实时点播转码、云台 PTZ 智能控制、双向语音对讲、云端录像回放与跨系统联动抓拍**的全套流媒体中枢能力。

---

## 🏗️ 视频流转总体架构

```text
 ┌────────────────────────────────────────────────────────┐
 │            前端播放层 (Vue 3 + Jessibuca / WFS)        │
 └───────────────────────────┬────────────────────────────┘
                             │ WebRTC / HTTP-FLV / WS-FLV / HLS / WebSocket
                             ▼
 ┌────────────────────────────────────────────────────────┐
 │           ZLMediaKit 流媒体服务集群 (Media Server)      │
 │  · 动态 RTP 端口分配 (30000-30500)                      │
 │  · 多协议拉流/推流代理 (StreamProxy / StreamPush)       │
 │  · REST Webhook 回调通知 (on_publish / on_play 等)     │
 └─────────────┬────────────────────────────▲─────────────┘
               │ PS / ES / RTP 媒体流       │ REST Hook 控制
               │                            ▼
 ┌─────────────┴────────┐      ┌──────────────────────────┐
 │ GB28181 监控设备 /IPC │      │   yudao-module-media     │
 │ (海康 / 大华 / 宇视) │      │  (JAIN-SIP 信令交互中枢)  │
 └─────────────▲────────┘      └────────────┬─────────────┘
               │ SIP 信令 (5060 UDP/TCP)    │ Spring Event
               └────────────────────────────┘
                                            ▼
                               ┌──────────────────────────┐
                               │  跨模块联动总线           │
                               │  (IotMediaLinkageEvent)  │
                               └──────────────────────────┘
```

---

## 📂 核心包目录结构

```text
cn.iocoder.yudao.module.media
├── common             # 媒体模块通用枚举与基类 (VideoCodecType, DeviceStreamStatus 等)
├── conf               # 国标 SIP 与流媒体服务器配置 (MediaConfig, SipConfig)
├── framework          # 框架扩展 (SIP 监听器装配, Webhook 认证切面)
├── gb28181            # ★ GB/T 28181 核心信令协议栈
│   ├── auth           # SIP 注册 Digest 摘要鉴权
│   ├── command        # SIP 指令发送器 (INVITE, BYE, MESSAGE, INFO, ACK)
│   ├── event          # SIP 报文事件分发与解析
│   ├── session        # SIP 会话管理与动态 RTP 端口分配
│   ├── transmit       # UDP / TCP 双栈 SIP 传输层
│   └── xml            # 设备目录、状态、报警等 XML 消息反序列化
├── jt1078             # JT/T 1078 车载音视频扩展
├── media              # ★ ZLMediaKit REST API 客户端与 Hook 回调处理器
├── service            # 业务服务层接口与实现 (DeviceService, ChannelService, PlayService 等)
├── storager           # 媒体数据访问与持久化存储层
├── streamProxy        # 外部 RTSP/RTMP/HTTP 拉流代理服务
├── streamPush         # 自主推流分发服务
├── vmanager           # REST Controller 控制器层 (点播、PTZ、录像、回放)
└── web                # Web 交互与 WebSocket 状态广播
```

---

## 💾 核心数据库表结构 (`sql/module/media-2026-11-12.sql`)

| 数据表名 | 实体业务定位 | 核心关键字段 | 说明 |
| :--- | :--- | :--- | :--- |
| **`mz_device`** | 国标/监控设备主表 | `device_id`, `name`, `ip`, `port`, `transport`, `on_line`, `keepalive_time` | 记录国标 20 位编码、SIP 通信协议（UDP/TCP）及心跳状态。 |
| **`mz_device_channel`** | 设备物理/逻辑通道 | `gb_device_id`, `device_id`, `name`, `ptz_type`, `longitude`, `latitude`, `status` | 通道类型包含 132(摄像机)、131(报警输入)等，PTZ 类型决定是否有云台。 |
| **`mz_media_server`** | ZLMediaKit 节点 | `id`, `ip`, `http_port`, `rtmp_port`, `rtp_proxy_port`, `secret`, `hook_ip` | 流媒体服务器配置，支持单机或多节点分布式挂载。 |
| **`mz_platform`** | 上级国标级联平台 | `server_gb_id`, `server_ip`, `server_port`, `username`, `password`, `enable` | 向上级应急、安防或公安平台级联推送设备目录与视频流。 |
| **`mz_platform_channel`** | 级联通道映射 | `platform_id`, `device_channel_id`, `custom_device_id` | 映射允许上报给上级平台的通道列表。 |
| **`mz_stream_proxy`** | 拉流代理任务 | `app`, `stream`, `src_url`, `enable_audio`, `enable_mp4`, `pulling` | 将第三方 RTSP/RTMP 摄像头拉取到平台内统一转码分发。 |
| **`mz_stream_push`** | 自主推流任务 | `app`, `stream`, `push_time`, `pushing` | 接收无人机、推流软件等 RTMP/RTSP 推流。 |
| **`mz_cloud_record`** | 云端录像记录 | `app`, `stream`, `file_path`, `file_size`, `start_time`, `end_time` | ZLMediaKit 云端 MP4 切片录像索引。 |
| **`mz_record_plan`** | 定时录像计划 | `name`, `snap` | 针对特定通道配置 7x24 小时或按周定时录像策略。 |
| **`mz_device_onvif`** | ONVIF 设备配置表 | `name`, `ip`, `port`, `username`, `password`, `rtsp_url` | ONVIF 协议自动探测与 RTSP 流媒体代理。 |
| **`mz_device_hik`** | 海康威视 ISAPI 表 | `name`, `ip`, `port`, `username`, `password`, `serial_number` | 海康威视专用设备管理与 ISAPI 信令交互。 |
| **`mz_device_dahua`** | 大华私有协议表 | `name`, `ip`, `port`, `username`, `password`, `channel_count` | 大华专用设备 RPC 交互与流媒体代理。 |
| **`mz_device_ezviz`** | 萤石云开放平台表 | `name`, `app_key`, `app_secret`, `device_serial`, `channel_no` | 萤石云 OpenAPI 实时流拉取。 |
| **`mz_device_jt1078`** | JT/T 1078 车载终端表 | `sim_no`, `plate_no`, `protocol_version`, `channel_count` | 交通部车载音视频终端协议交互。 |

---

## ⚡ GB/T 28181 信令交互全流程

### 1. 设备注册与鉴权 (Registration)
- 摄像机向 SIP 端口（默认 `5060`）发送未带认证的 `REGISTER` 请求；
- 本地 SIP Stack 响应 `401 Unauthorized` 并携带随机 `nonce`；
- 设备使用 MD5 算法对密码进行摘要加密后重新发送 `REGISTER`；
- 平台校验通过，写入/刷新 Redis 会话缓存并更新 `mz_device` 状态为在线。

### 2. 心跳保活 (Keepalive)
- 摄像机周期性发送 SIP `MESSAGE` 请求，载荷包含 `<CmdType>Keepalive</CmdType>`；
- 平台更新设备最后保活时间；连续 3 次心跳超时触发离线告警并注销会话。

### 3. 通道目录拉取 (Catalog)
- 平台向设备下发 `Catalog` 查询命令；
- 设备通过 XML 报文分批上报通道清单（支持大批量分包拉取与增量更新）。

### 4. 实时点播推流 (Live Streaming)
```text
前端请求播放 -> 平台请求 ZLMediaKit 分配动态 RTP 端口 (30000-30500)
             -> 平台向设备发送 SIP INVITE (附带 ZLM 的媒体接收 SDP)
             -> 设备返回 200 OK (携带设备推流媒体 SDP)
             -> 平台返回 SIP ACK，设备开始向 ZLMediaKit 推送 RTP 流
             -> ZLMediaKit 接收 RTP 并转码为 WebRTC / HTTP-FLV / HLS
             -> 前端播放器通过返回的低延迟 URL 开始流畅播放
```

### 5. 云台控制与预置位联动 (PTZ Control)
- 支持八向转动、焦距拉远拉近、光圈调节；
- 支持预置位设置、调用与删除；**预置位调用指令码：`129`（十六进制 `0x81`）**；
- 跨系统联动场景下，系统通过 Spring 事件总线监听 `IotMediaLinkageEvent`，自动下发云台旋转与抓拍信令。

---

## 🛠️ 配置项说明 (`application.yaml`)

```yaml
sip:
  ip: ${SIP_IP:192.168.1.100}         # SIP 服务器监听 IP (必须为设备可直接访问的实际 IP)
  port: 5060                          # SIP 端口 (UDP / TCP)
  id: 34020000002000000001            # SIP 服务端国标编码 (20位)
  domain: 3402000000                  # SIP 域 (前10位)
  password: admin                     # 接入默认设备鉴权密码
  ptime: 20                           # 抓包打包时长
  timeout: 5                          # 信令响应超时时长 (秒)

media:
  id: default                         # 默认流媒体服务器 ID
  ip: ${MEDIA_IP:192.168.1.100}       # ZLMediaKit 宿主机 IP
  http-port: 80                       # ZLMediaKit HTTP 端口
  secret: 035c73f7-bb6b-4889-a715-d9eb2d1925cc # REST API 访问秘钥
  hook-ip: ${LOCAL_IP:192.168.1.100}  # ZLMediaKit 回调当前 yudao-server 的 IP
```

---

## ⚠️ 常见排错指南

1. **设备注册显示在线，但点播提示超时或黑屏**：
   - 检查防火墙是否放行了 ZLMediaKit 的动态 RTP 端口范围（`30000-30500`）；
   - 确认 `sip.ip` 与 `media.ip` 不能为 `127.0.0.1`，必须是设备能直接路由的真实局域网或公网 IP。
2. **海康/大华摄像头通道列表获取不全**：
   - 检查摄像头国标编码是否规范（第 11-13 位必须符合国标规定）；
   - 检查网络 MTU 设置，避免大 XML 分包在 UDP 下被路由器丢弃（可切换为 TCP 传输信令）。
