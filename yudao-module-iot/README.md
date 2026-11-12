# MzMedia - 物联网引擎与多协议网关 (`yudao-module-iot`)

## 📌 模块定位与职责

`yudao-module-iot` 是 MzMedia 的核心物联网接入中枢，致力于解决中小企业数字化转型中普遍存在的 **“工控协议繁多、非标设备难连、宽带网关割裂、私有报文难解、数据流转阻滞”** 难题。

模块基于清晰的三层解耦架构打造，内置 **15+ 种工业、物联与宽带网络通信协议栈**（全面支持 **TR-069 CWMP** 与 **TR-369 USP**），提供从**物模型（TSL）配置、动态设备注册、设备影子、Modbus 点位轮询调度、TR-069/TR-369 宽带终端集中管控、双核心规则引擎（数据分发 + 场景联动）到 TDengine 海量时序存储**的完整端到端能力。

---

## 🏗️ 整体分层架构

```text
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         yudao-module-iot-biz (业务中枢层)                   │
 │   · 设备全生命周期 (IotDevice)         · 工业 Modbus 点位调度 (IotModbus)      │
 │   · 产品与品类管理 (IotProduct)        · 远程 OTA 固件升级 (IotOta)            │
 │   · 物模型 TSL 管理 (IotThingModel)    · 阈值告警配置与履历 (IotAlert)         │
 │   ★ TR-069 CWMP 管理 (IotDeviceCwmp)   · TR-369 USP 终端控制 (IotDeviceUsp)   │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │   ★ 双核心规则引擎：                                                        │
 │   1. 数据分发引擎 (IotDataRule): SpEL 过滤 -> 9 种 Sink 输出 (TDengine/MQ等) │
 │   2. 场景联动引擎 (IotSceneRule): Trigger -> Condition -> Action (联动视频等)│
 ├─────────────────────────────────────────────────────────────────────────────┤
 │                     yudao-module-iot-core (核心模型与总线层)                │
 │   · IotMessageBus (消息总线抽象: Redis Stream / Kafka / RocketMQ)          │
 │   · TSL 物模型规范 (Property/Service/Event)   · 设备消息体 (IotDeviceMessage) │
 │   ★ TR-069 SOAP 编解码 (CwmpSoapParser/Builder)                            │
 │   ★ TR-369 Protobuf 编解码 (UspProtobufCodec/Builder)                       │
 │   ★ TR-181/TR-098 物模型统一映射器 (Tr181ThingModelMapper)                  │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │                   yudao-module-iot-gateway (多协议网关接入层)              │
 │   · MQTT / EMQX 5.0 (长连接接入)     · Modbus TCP / RTU (工业 PLC / 仪表)    │
 │   · CoAP (窄带 NB-IoT / 电池传感器)   · TCP / UDP (私有定长/分隔符报文)       │
 │   · HTTP REST / WebHook (主动推送)    · WebSocket (双全工实时推送)           │
 │   ★ TR-069 ACS (Vert.x HTTP :9292 / Digest 认证 / 会话状态机 / 反向唤醒)    │
 │   ★ TR-369 MTP (Vert.x MQTT :18833 / WebSocket / Protobuf Record 路由)     │
 └─────────────────────────────────────────────────────────────────────────────┘
```

---

## 📂 子模块职责划分

| 子模块工程 | 核心业务职责 | 关键类与包路径 |
| :--- | :--- | :--- |
| **`yudao-module-iot-core`** | 核心数据模型、消息格式与总线抽象；TR-069 SOAP XML 编解码、TR-369 Protobuf 二进制编解码及 TR-181 物模型映射器 | `cn.iocoder.yudao.module.iot.core.protocol.cwmp` (SOAP 编解码器)<br/>`cn.iocoder.yudao.module.iot.core.protocol.usp` (Protobuf 编解码器)<br/>`cn.iocoder.yudao.module.iot.core.protocol.mapper.Tr181ThingModelMapper`<br/>`cn.iocoder.yudao.module.iot.core.mq.IotMessageBus` |
| **`yudao-module-iot-gateway`**| 15+ 种通信协议网关实现，网络会话状态机，协议下行消息订阅与转化 | `cn.iocoder.yudao.module.iot.gateway.protocol.cwmp` (ACS 服务、会话队列、主动唤醒客户端)<br/>`cn.iocoder.yudao.module.iot.gateway.protocol.usp` (MQTT/WS MTP 实现)<br/>`cn.iocoder.yudao.module.iot.gateway.protocol` (mqtt, modbus, coap, emqx, http, tcp, udp, websocket) |
| **`yudao-module-iot-biz`** | 业务接口、Controller、DAL、规则引擎、TR-069/TR-369 管理服务及 OTA 任务联动 | `cn.iocoder.yudao.module.iot.service.device.IotDeviceCwmpService`<br/>`cn.iocoder.yudao.module.iot.service.device.IotDeviceUspService`<br/>`cn.iocoder.yudao.module.iot.service.ota.IotOtaTaskRecordServiceImpl`<br/>`cn.iocoder.yudao.module.iot.service.rule` |

---

## 📡 15+ 种支持协议矩阵

| 协议分类 | 通信协议 | 运行模式 | 典型应用硬件 |
| :--- | :--- | :--- | :--- |
| **宽带网关与家庭网络 (BBF)** | **TR-069 (CWMP)** | ACS HTTP Server (:9292) / SOAP XML | 运营商光猫 (ONT/ONU)、家庭宽带网关、企业级路由器、VoIP 语音网关。 |
| **宽带网关与家庭网络 (BBF)** | **TR-369 (USP)** | MQTT MTP (:18833) / WebSocket / Protobuf | 新一代 Wi-Fi 7 智能路由器、多服务网关、边缘容器网关、智能家居控制器。 |
| **轻量级物联网** | **MQTT (3.1/3.1.1/5.0)** | Broker / Client | EMQX 5.0 集成，智能家居、环境传感、充电桩、物联网网关。 |
| **工业工控总线** | **Modbus TCP / RTU** | Master 主动轮询 / Slave 被动 | PLC 控制器、智能电度表、温湿度变送器、数显仪、继电器模块。 |
| **低功耗窄带网络** | **CoAP (Californium)** | UDP / 紧凑二进制 | NB-IoT 水表燃气表、野外水文监测、电池供电低功耗传感器。 |
| **通用网络接口** | **HTTP / HTTPS REST** | Webhook / API 推送 | 具有 HTTP 传输能力的智能终端、三方云平台中继推送。 |
| **实时长连接** | **WebSocket** | 全双工双向通讯 | 大屏前端指令双向推送、移动端即时状态监听。 |
| **原生底层传输** | **TCP / UDP** | 自定义编解码器 | 老旧仪器仪表私有协议，支持定长、分隔符、长度字段及 AI 自动推导解码器。 |

---

## 🌐 宽带论坛 TR-069 (CWMP) 与 TR-369 (USP) 深度集成

为满足智慧园区、企业园区、运营商入网等场景下网络设备统一管控的诉求，MzMedia 深度融合了 Broadband Forum（宽带论坛）的两大核心终端管理协议：

### 1. TR-069 (CWMP: CPE WAN Management Protocol)
- **ACS 服务端实现**：基于 Vert.x Web 暴露标准 ACS 接入端点（默认 `:9292/cwmp`），支持 HTTP Basic 与 Digest 鉴权。
- **全生命周期会话状态机**：
  - 自动响应 CPE 发起的 `Inform` 报文（涵盖 `0 BOOTSTRAP`, `1 BOOT`, `2 PERIODIC`, `4 VALUE CHANGE`, `7 TRANSFER COMPLETE` 等事件）。
  - 通过会话 Cookie 维护会话上下文，在 CPE 空 POST 时出队下发暂存命令，直到全部指令执行完毕回复 HTTP 204 关闭会话。
- **反向主动唤醒 (Connection Request)**：
  - 集成 `IotCwmpConnectionRequestClient`（基于 OkHttp + okhttp-digest），通过 HTTP GET 主动触发 CPE 的 `ConnectionRequestURL`，打破 CPE 仅在周期上报时被动受控的限制。
- **功能特性集**：
  - **参数树动态发现**：下发 `GetParameterNames` 遍历设备支持的 TR-098 / TR-181 完整参数树。
  - **参数读写**：下发 `GetParameterValues` 读取实时状态，下发 `SetParameterValues` 修改设备配置。
  - **远程控制**：支持远程安全重启 (`Reboot`)、恢复出厂设置 (`FactoryReset`)。
  - **业务快捷面板**：内置 Wi-Fi 快速配置卡片（双频 SSID、密码、加密模式）、Ping 网络诊断测试工具。

### 2. TR-369 (USP: User Services Platform)
- **现代化双向通信架构**：
  - 采用轻量、异步的 USP 架构，默认支持 **MQTT MTP**（基于内嵌 Vert.x MQTT Server `:18833`，Agent 订阅 `agent/{endpointId}`，Controller 监听 `controller/{endpointId}`），并支持 WebSocket MTP。
- **高性能 Protobuf 编解码**：
  - 封装 `UspProtobufCodec`，基于 Protobuf 标准 wire format 实现 `usp-record` 与 `usp-msg` 二进制的快速序列化与反序列化，无需额外依赖系统级 `protoc` 编译工具。
- **功能特性集**：
  - **USP Get**：异步查询 `Device.` 树下的对象与属性值。
  - **USP Set**：批量或单项设置参数，支持原子部分更新（AllowPartial）。
  - **USP Operate**：远程执行标准化操作，如 `Device.Reboot()`、`Device.FactoryReset()`、`Device.Download()`、`Device.IP.Diagnostics.IPPing()` 等，并支持自定义参数输入。
  - **USP Notify**：接收 Agent 主动上报的属性变更与事件通知。

### 3. TR-181 统一物模型 (TSL) 映射与 OTA 升级调度
- **自动物模型映射 (`Tr181ThingModelMapper`)**：
  - 将复杂的 TR-069/TR-181 节点路径（如 `DeviceInfo.SoftwareVersion`、`ExternalIPAddress`、`WLANConfiguration.1.SSID`）自动双向映射为平台统一属性（`firmwareVersion`、`wanIp`、`wifiSsid` 等），使宽带设备天然融入平台的**数据分发规则引擎**与**安防视频联动**体系中。
- **固件调度全闭环 (`IotOtaTaskRecordServiceImpl`)**：
  - 在平台下发 OTA 升级任务时，自动根据设备协议识别下发通道：
    - `TR069` 设备：自动组装并下发 `<cwmp:Download>` RPC（FileType 为 `"1 Firmware Upgrade Image"`）；
    - `TR369` 设备：自动组装并下发 `Device.Download()` 操作命令；
  - 设备完成固件更新后，通过 `TransferComplete` 或版本变更自动同步更新升级进度与状态。

---

## ⚙️ 规则引擎双核心技术规格

### 1. 数据分发引擎 (`IotDataRule`)
- **条件过滤**：支持通过 **SpEL 表达式** 或 **JSONPath** 对上报的报文进行毫秒级动态过滤（例如 `#payload.temperature > 45.0 && #payload.voltage < 210`）。
- **9 种 Sink 输出通道**：
  1. `TDengine`：列式时序数据库写入，超强压缩与高并发写入；
  2. `MySQL / PostgreSQL`：结构化关系数据归档；
  3. `Redis Stream`：内存流快速缓存与下游低延迟消费；
  4. `Kafka` / `RocketMQ` / `RabbitMQ`：企业级分布式消息管道；
  5. `HTTP Webhook`：主动回调推送至第三方系统；
  6. `MQTT Broker`：重新发布至指定 Topic，驱动边缘节点执行联动；
  7. `TCP / WebSocket`：透传至指定的网络连接链路。

### 2. 场景联动引擎 (`IotSceneRule` - TCA 架构)
- **触发器 (Trigger)**：
  - 属性上报触发 (`IotDevicePropertyPostTriggerMatcher`)
  - 事件上报触发 (`IotDeviceEventPostTriggerMatcher`)
  - 设备状态更新 (`IotDeviceStateUpdateTriggerMatcher`)
  - 定时调度触发 (CRON)
- **条件评估 (Condition)**：
  - 属性阈值比较（大于、小于、等于、区间）
  - 生效时间段限制（如“仅在工作日 18:00 - 次日 08:00 生效”）
- **动作执行器 (Action)**：
  - 下发设备属性 (`IotDevicePropertySetSceneRuleAction`)
  - 调用设备服务 (`IotDeviceServiceInvokeSceneRuleAction`)
  - 触发系统告警 (`IotAlertTriggerSceneRuleAction`)
  - **★ 视频监控联动 (`IotMediaSceneRuleAction`)**：向系统总线发布 `IotMediaLinkageEvent`，依据设备前缀多态联动对应摄像头（全面支持 GB28181、ONVIF、海康威视、大华、萤石云、部标 JT1078）旋转至预置位（指令码 `129` 即 `0x81` 或相应协议 API）并启动录像与现场抓拍。

---

## 💾 核心数据库表结构 (`sql/iot_tr069_tr369.sql` & `sql/module/iot-*.sql`)

- `iot_product`：物联产品定义（产品标识 ProductKey、设备类型 `deviceType`: 0直连/1网关子设备/2网关/3视频监控设备、数据格式 JSON/Custom、网络类型、接入网关）。
- `iot_device`：设备实体表（设备名称、DeviceSecret、在线状态、最后活跃时间、设备影子 JSON、父子网关拓扑、`video_config` 视频流媒体配置）。
- `iot_thing_model`：物模型 TSL 定义（属性、服务、事件，数据类型支持 int, float, string, bool, enum, struct, array）。
- `iot_device_modbus_config` / `iot_device_modbus_point`：工业 Modbus 采集点位表（从站地址 SlaveId、轮询周期、寄存器类型 01/02/03/04、寄存器偏移量、缩放系数、字节序）。
- **`iot_device_cwmp_config`**：TR-069 CPE 连接配置表（ACS 认证用户名密码、ConnectionRequestURL 及凭证、周期上报参数、数据模型规范 TR098/TR181）。
- **`iot_device_cwmp_param`**：TR-069 参数快照表（节点路径、当前值、数据类型、读写权限）。
- **`iot_device_usp_config`**：TR-369 USP 配置表（Agent Endpoint ID、Controller Endpoint ID、MTP 类型、发布与订阅主题、协议版本）。
- **`iot_device_usp_param`**：TR-369 USP 参数快照表（TR-181 路径、当前值、数据类型、读写权限）。
- `iot_data_rule` / `iot_data_sink`：数据流转过滤规则与 9 类目的地配置。
- `iot_scene_rule`：TCA 场景联动规则配置。
- `iot_alert_config` / `iot_alert_record`：告警规则阈值配置与历史告警流水。
- `iot_ota_firmware` / `iot_ota_task` / `iot_ota_task_record`：OTA 固件包与批次分发升级任务。
