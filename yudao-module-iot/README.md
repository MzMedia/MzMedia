# MzMedia - 物联网引擎与多协议网关 (`yudao-module-iot`)

## 📌 模块定位与职责

`yudao-module-iot` 是 MzMedia 的核心物联网接入中枢，致力于解决中小企业数字化转型中普遍存在的 **“工控协议繁多、非标设备难连、私有报文难解、数据流转阻滞”** 难题。

模块基于清晰的三层解耦架构打造，内置 **13+ 种工业与物联通信协议栈**，提供从**物模型（TSL）配置、动态设备注册、设备影子、Modbus 点位轮询调度、双核心规则引擎（数据分发 + 场景联动）到 TDengine 海量时序存储**的完整端到端能力。

---

## 🏗️ 整体分层架构

```text
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                         yudao-module-iot-biz (业务中枢层)                   │
 │   · 设备全生命周期 (IotDevice)         · 工业 Modbus 点位调度 (IotModbus)      │
 │   · 产品与品类管理 (IotProduct)        · 远程 OTA 固件升级 (IotOta)            │
 │   · 物模型 TSL 管理 (IotThingModel)    · 阈值告警配置与履历 (IotAlert)         │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │   ★ 双核心规则引擎：                                                        │
 │   1. 数据分发引擎 (IotDataRule): SpEL 过滤 -> 9 种 Sink 输出 (TDengine/MQ等) │
 │   2. 场景联动引擎 (IotSceneRule): Trigger -> Condition -> Action (联动视频等)│
 ├─────────────────────────────────────────────────────────────────────────────┤
 │                     yudao-module-iot-core (核心模型与总线层)                │
 │   · IotMessageBus (消息总线抽象: Redis Stream / Kafka / RocketMQ)          │
 │   · TSL 物模型规范 (Property/Service/Event)   · 设备消息体 (IotDeviceMessage) │
 ├─────────────────────────────────────────────────────────────────────────────┤
 │                   yudao-module-iot-gateway (多协议网关接入层)              │
 │   · MQTT / EMQX 5.0 (长连接接入)     · Modbus TCP / RTU (工业 PLC / 仪表)    │
 │   · CoAP (窄带 NB-IoT / 电池传感器)   · TCP / UDP (私有定长/分隔符报文)       │
 │   · HTTP REST / WebHook (主动推送)    · WebSocket (双全工实时推送)           │
 └─────────────────────────────────────────────────────────────────────────────┘
```

---

## 📂 子模块职责划分

| 子模块工程 | 核心业务职责 | 关键类与包路径 |
| :--- | :--- | :--- |
| **`yudao-module-iot-core`** | 核心数据模型、消息格式与总线抽象 | `cn.iocoder.yudao.module.iot.core.model.thing`<br/>`cn.iocoder.yudao.module.iot.core.mq.IotMessageBus`<br/>`cn.iocoder.yudao.module.iot.core.message.IotDeviceMessage` |
| **`yudao-module-iot-gateway`**| 13+ 种通信协议网关实现与编解码器 | `cn.iocoder.yudao.module.iot.gateway.protocol` (mqtt, modbus, coap, emqx, http, tcp, udp, websocket) |
| **`yudao-module-iot-biz`** | 业务接口、Controller、DAL、规则引擎与定时轮询 | `cn.iocoder.yudao.module.iot.biz.service.device`<br/>`cn.iocoder.yudao.module.iot.biz.service.rule`<br/>`cn.iocoder.yudao.module.iot.biz.dal` |

---

## 📡 13+ 种支持协议矩阵

| 协议分类 | 通信协议 | 运行模式 | 典型应用硬件 |
| :--- | :--- | :--- | :--- |
| **轻量级物联网** | **MQTT (3.1/3.1.1/5.0)** | Broker / Client | EMQX 5.0 集成，智能家居、环境传感、充电桩、物联网网关。 |
| **工业工控总线** | **Modbus TCP / RTU** | Master 主动轮询 / Slave 被动 | PLC 控制器、智能电度表、温湿度变送器、数显仪、继电器模块。 |
| **低功耗窄带网络** | **CoAP (Californium)** | UDP / 紧凑二进制 | NB-IoT 水表燃气表、野外水文监测、电池供电低功耗传感器。 |
| **通用网络接口** | **HTTP / HTTPS REST** | Webhook / API 推送 | 具有 HTTP 传输能力的智能终端、三方云平台中继推送。 |
| **实时长连接** | **WebSocket** | 全双工双向通讯 | 大屏前端指令双向推送、移动端即时状态监听。 |
| **原生底层传输** | **TCP / UDP** | 自定义编解码器 | 老旧仪器仪表私有协议，支持定长、分隔符、长度字段及 AI 自动推导解码器。 |

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
  - **★ 视频监控联动 (`IotMediaSceneRuleAction`)**：向系统总线发布 `IotMediaLinkageEvent`，命令指定 GB28181 摄像头旋转至预置位（指令码 `129` 即 `0x81`）并启动录像抓拍。

---

## 💾 核心数据库表结构 (`sql/module/iot-*.sql`)

- `iot_product`：物联产品定义（产品标识 ProductKey、数据格式 JSON/Custom、网络类型、接入网关）。
- `iot_device`：设备实体表（设备名称、DeviceSecret、在线状态、最后活跃时间、设备影子 JSON、父子网关拓扑）。
- `iot_thing_model`：物模型 TSL 定义（属性、服务、事件，数据类型支持 int, float, string, bool, enum, struct, array）。
- `iot_device_modbus_config` / `iot_device_modbus_point`：工业 Modbus 采集点位表（从站地址 SlaveId、轮询周期、寄存器类型 01/02/03/04、寄存器偏移量、缩放系数、字节序）。
- `iot_data_rule` / `iot_data_sink`：数据流转过滤规则与 9 类目的地配置。
- `iot_scene_rule`：TCA 场景联动规则配置。
- `iot_alert_config` / `iot_alert_record`：告警规则阈值配置与历史告警流水。
- `iot_ota_firmware` / `iot_ota_task`：OTA 固件包与批次分发升级任务。
