<p align="center">
 <img src="https://img.shields.io/badge/Spring%20Boot-4.1.2-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/badge/Vue-3.4-blue.svg" alt="Downloads">
 <img src="https://img.shields.io/github/license/YunaiV/ruoyi-vue-pro" alt="Downloads" />
</p>

**严肃声明：现在、未来都不会有商业版本，所有代码全部开源!！**

**「我喜欢写代码，乐此不疲」**  
**「我喜欢做开源，以此为乐」**

我 🐶 在上海艰苦奋斗，早中晚在 top3 大厂认真搬砖，夜里为开源做贡献。

如果这个项目让你有所收获，记得 Star 关注哦，这对我是非常不错的鼓励与支持。

## 🐶 新手必读

* 演示地址【Vue3 + element-plus】：<http://dashboard-vue3.yudao.iocoder.cn>
* 演示地址【Vue3 + vben(ant-design-vue)】：<http://dashboard-vben.yudao.iocoder.cn>
* 演示地址【Vue2 + element-ui】：<http://dashboard.yudao.iocoder.cn>
* 启动文档：<https://doc.iocoder.cn/quick-start/>
* 视频教程：<https://doc.iocoder.cn/video/>

## 🐰 版本说明

| 版本                                                                  | JDK 8 + Spring Boot 2.7                                                   | JDK 17/21 + Spring Boot 3.5                                                           | JDK 25 + Spring Boot 4.x                                                              |
|---------------------------------------------------------------------|---------------------------------------------------------------------------|---------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| 【完整版】[ruoyi-vue-pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro) | [`master`](https://gitee.com/zhijiantianya/ruoyi-vue-pro/tree/master/) 分支 | [`master-jdk17`](https://gitee.com/zhijiantianya/ruoyi-vue-pro/tree/master-jdk17/) 分支 | [`master-jdk25`](https://gitee.com/zhijiantianya/ruoyi-vue-pro/tree/master-jdk25/) 分支 |
| 【精简版】[yudao-boot-mini](https://gitee.com/yudaocode/yudao-boot-mini) | [`master`](https://gitee.com/yudaocode/yudao-boot-mini/tree/master/) 分支   | [`master-jdk17`](https://gitee.com/yudaocode/yudao-boot-mini/tree/master-jdk17/) 分支   | [`master-jdk25`](https://gitee.com/yudaocode/yudao-boot-mini/tree/master-jdk25/) 分支   |

* 【完整版】：包括系统功能、基础设施、视频监控 (GB28181/Media)、IoT 物联网、AI 大模型与智能体 (AI/Spring AI Agent)、支付系统、数据报表、微信公众号、MES 执行制造系统、WMS 仓库管理、PMS 项目管理等功能
* 【精简版】：只包括系统功能、基础设施功能，不包括视频监控、IoT 物联网、AI 大模型与智能体、支付系统、数据报表、微信公众号、MES、WMS、PMS 等业务扩展功能

可参考 [《迁移文档》](https://doc.iocoder.cn/migrate-module/) ，只需要 5-10 分钟，即可将【完整版】按需迁移到【精简版】

## 🐯 平台简介

**芋道**，以开发者为中心，打造中国第一流的快速开发平台，全部开源，个人与企业可 100% 免费使用。

> 有任何问题，或者想要的功能，可以在 _Issues_ 中提给艿艿。
>
> 😜 给项目点点 Star 吧，这对我们真的很重要！

![架构图](/.image/common/ruoyi-vue-pro-architecture.png)

* Java 后端：`master` 分支为 JDK 8 + Spring Boot 2.7，`master-jdk17` 分支为 JDK 17/21 + Spring Boot 3.5，`master-jdk25` 分支为 JDK 25 + Spring Boot 4.x
* 管理后台的电脑端：Vue3 提供 `element-plus`、`vben(ant-design-vue)` 两个版本，Vue2 提供 `element-ui` 版本
* 管理后台的移动端：采用 `uni-app` 方案，一份代码多终端适配，同时支持 APP、小程序、H5！
* 后端采用 Spring Boot 多模块架构、MySQL + MyBatis Plus、Redis + Redisson
* 数据库可使用 MySQL、Oracle、PostgreSQL、SQL Server、MariaDB，以及达梦、人大金仓、瀚高等国产数据库，详见 [信创兼容与数据库支持](#-信创兼容与数据库支持)
* 消息队列可使用 Event、Redis、RabbitMQ、Kafka、RocketMQ 等
* 权限认证使用 Spring Security & Token & Redis，支持多终端、多种用户的认证系统，支持 SSO 单点登录
* 支持加载动态权限菜单，按钮级别权限控制，Redis 缓存提升性能
* 支持 SaaS 多租户，可自定义每个租户的权限，提供透明化的多租户底层封装
* 视频监控深度融合 WVP-GB28181-Pro 与 ZLMediaKit，支持 GB28181 国标协议接入、云台控制 (PTZ)、语音对讲、多分屏实时点播与录像回放
* 物联网支持设备管理、物模型定义、规则引擎及视频监控设备联动
* AI 模块支持主流大模型接入、流式对话、思维导图、多模态以及基于 Spring AI 的 Agent 智能体编排与 RAG 知识库检索
* 高效率开发，使用代码生成器可以一键生成 Java、Vue 前后端代码、SQL 脚本、接口文档，支持单表、树表、主子表
* 实时通信，采用 Spring WebSocket 实现，内置 Token 身份校验，支持 WebSocket 集群
* 集成微信小程序、微信公众号、企业微信、钉钉等三方登陆，集成支付宝、微信等支付与退款
* 集成阿里云、腾讯云等短信渠道，集成 MinIO、阿里云、腾讯云、七牛云等云存储服务
* 集成报表设计器、大屏设计器，通过拖拽即可生成酷炫的报表与大屏

## 🐲 信创兼容与数据库支持

支持国产操作系统、数据库和中间件，满足信创环境的部署需要：

| 类别       | 支持                                                  | 说明                                                                                                                                                         |
|----------|-----------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 国产操作系统   | 银河麒麟、统信 UOS 等 Linux 系统                              | 参考 [《Linux 部署》](https://doc.iocoder.cn/deployment-linux/)，选择与 CPU 架构匹配的 JDK                                                                              |
| 关系型数据库   | MySQL、MariaDB、Oracle、PostgreSQL、SQL Server          | 默认使用 MySQL                                                                                                                                                 |
| 国产关系型数据库 | 达梦 DM、人大金仓 KingbaseES、openGauss、瀚高 HighGo、OceanBase、TiDB、华为 GaussDB、云和恩墨 MogDB、海量 Vastbase G100、PolarDB、神通 | 参考 [《国产信创数据库》](https://doc.iocoder.cn/xinchuang-db/) |
| 国产缓存     | 东方通 TongRDS、宝兰德 BES CacheDB                         | 替换 Redis，参考 [《国产信创数据库》](https://doc.iocoder.cn/xinchuang-db/)                                                               |
| 国产应用服务器  | 东方通 TongWeb、宝兰德 BES AppServer                       | 替换 Tomcat，参考 [《东方通服务器部署》](https://doc.iocoder.cn/deployment-tong/)、[《宝兰德服务器部署》](https://doc.iocoder.cn/deployment-bes/) |
| 国产 Web 服务器 | 东方通 TongHttpServer                                   | 替换 Nginx，提供静态资源、反向代理，参考 [《东方通服务器部署》](https://doc.iocoder.cn/deployment-tong/)                                                                       |

* 数据库脚本：[`sql`](sql/) 目录提供 MySQL、Oracle、PostgreSQL、SQL Server、达梦、人大金仓、openGauss、瀚高、OceanBase 的项目初始化脚本（含 Quartz），优先使用对应目录；MariaDB、TiDB 直接使用 MySQL 脚本；GaussDB、MogDB、Vastbase G100 基于 openGauss 脚本导入，PolarDB PostgreSQL 版直接使用 PostgreSQL 脚本；神通需按 [文档](https://doc.iocoder.cn/xinchuang-db/#_9-神通数据库) 调整 openGauss 项目脚本（含 Quartz）后导入
* 其它数据库、后续表结构变更，可参考 [`sql/tools`](sql/tools/README.md) 的转换工具及 [《表结构变更（版本升级）》](https://doc.iocoder.cn/sql-update/#_3-数据库转换)，转换后仍需按实际数据库调整、测试

##  🐳 项目关系

![架构演进](/.image/common/yudao-roadmap.png)

三个项目的功能对比，可见社区共同整理的 [国产开源项目对比](https://www.yuque.com/xiatian-bsgny/lm0ec1/wqf8mn) 表格。

### 后端项目

| 项目                                                              | Star                                                                                                                                                                                                                                                                                             | 简介                          |
|-----------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------|
| [ruoyi-vue-pro](https://gitee.com/zhijiantianya/ruoyi-vue-pro)  | [![Gitee star](https://gitee.com/zhijiantianya/ruoyi-vue-pro/badge/star.svg?theme=white)](https://gitee.com/zhijiantianya/ruoyi-vue-pro) [![GitHub stars](https://img.shields.io/github/stars/YunaiV/ruoyi-vue-pro.svg?style=social&label=Stars)](https://github.com/YunaiV/ruoyi-vue-pro)       | 基于 Spring Boot 多模块架构        |
| [yudao-cloud](https://gitee.com/zhijiantianya/yudao-cloud)      | [![Gitee star](https://gitee.com/zhijiantianya/ruoyi-vue-pro/badge/star.svg?theme=white)](https://gitee.com/zhijiantianya/yudao-cloud) [![GitHub stars](https://img.shields.io/github/stars/YunaiV/yudao-cloud.svg?style=social&label=Stars)](https://github.com/YunaiV/yudao-cloud)               | 基于 Spring Cloud 微服务架构       |
| [Spring-Boot-Labs](https://gitee.com/yudaocode/SpringBoot-Labs) | [![Gitee star](https://gitee.com/yudaocode/SpringBoot-Labs/badge/star.svg?theme=white)](https://gitee.com/zhijiantianya/yudao-cloud) [![GitHub stars](https://img.shields.io/github/stars/yudaocode/SpringBoot-Labs.svg?style=social&label=Stars)](https://github.com/yudaocode/SpringBoot-Labs) | 系统学习 Spring Boot & Cloud 专栏 |

### 前端项目

| 项目                                                                         | Star                                                                                                                                                                                                                                                                                                                     | 简介                                     |
|----------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------|
| [ui (MzMedia UI)](ui/)                                                     | -                                                                                                                                                                                                                                                                                                                        | 基于 Vue3 + Element Plus 实现的视频联动物联网平台前端 |
| [yudao-ui-admin-vue3](https://gitee.com/yudaocode/yudao-ui-admin-vue3)     | [![Gitee star](https://gitee.com/yudaocode/yudao-ui-admin-vue3/badge/star.svg?theme=white)](https://gitee.com/yudaocode/yudao-ui-admin-vue3) [![GitHub stars](https://img.shields.io/github/stars/yudaocode/yudao-ui-admin-vue3.svg?style=social&label=Stars)](https://github.com/yudaocode/yudao-ui-admin-vue3)         | 基于 Vue3 + element-plus 实现的管理后台         |
| [yudao-ui-admin-vben](https://gitee.com/yudaocode/yudao-ui-admin-vben)     | [![Gitee star](https://gitee.com/yudaocode/yudao-ui-admin-vben/badge/star.svg?theme=white)](https://gitee.com/yudaocode/yudao-ui-admin-vben) [![GitHub stars](https://img.shields.io/github/stars/yudaocode/yudao-ui-admin-vben.svg?style=social&label=Stars)](https://github.com/yudaocode/yudao-ui-admin-vben)         | 基于 Vue3 + vben(ant-design-vue) 实现的管理后台 |
| [yudao-ui-admin-vue2](https://gitee.com/yudaocode/yudao-ui-admin-vue2)     | [![Gitee star](https://gitee.com/yudaocode/yudao-ui-admin-vue2/badge/star.svg?theme=white)](https://gitee.com/yudaocode/yudao-ui-admin-vue2) [![GitHub stars](https://img.shields.io/github/stars/yudaocode/yudao-ui-admin-vue2.svg?style=social&label=Stars)](https://github.com/yudaocode/yudao-ui-admin-vue2)         | 基于 Vue2 + element-ui 实现的管理后台           |
| [yudao-ui-admin-uniapp](https://gitee.com/yudaocode/yudao-ui-admin-uniapp) | [![Gitee star](https://gitee.com/yudaocode/yudao-ui-admin-uniapp/badge/star.svg?theme=white)](https://gitee.com/yudaocode/yudao-ui-admin-uniapp) [![GitHub stars](https://img.shields.io/github/stars/yudaocode/yudao-ui-admin-uniapp.svg?style=social&label=Stars)](https://github.com/yudaocode/yudao-ui-admin-uniapp) | 基于 Vue2 + element-ui 实现的管理后台           |
| [yudao-ui-go-view](https://gitee.com/yudaocode/yudao-ui-go-view)           | [![Gitee star](https://gitee.com/yudaocode/yudao-ui-go-view/badge/star.svg?theme=white)](https://gitee.com/yudaocode/yudao-ui-go-view) [![GitHub stars](https://img.shields.io/github/stars/yudaocode/yudao-ui-go-view.svg?style=social&label=Stars)](https://github.com/yudaocode/yudao-ui-go-view)                     | 基于 Vue3 + naive-ui 实现的大屏报表             |

## 😎 开源协议

**为什么推荐使用本项目？**

① 本项目采用比 Apache 2.0 更宽松的 [MIT License](https://gitee.com/zhijiantianya/ruoyi-vue-pro/blob/master/LICENSE) 开源协议，个人与企业可 100% 免费使用，不用保留类作者、Copyright 信息。

② 代码全部开源，不会像其他项目一样，只开源部分代码，让你无法了解整个项目的架构设计。[国产开源项目对比](https://www.yuque.com/xiatian-bsgny/lm0ec1/wqf8mn)

![开源项目对比](/.image/common/project-vs.png)

③ 代码整洁、架构整洁，遵循《阿里巴巴 Java 开发手册》规范，代码注释详细，113770 行 Java 代码，42462 行代码注释。

## 🤝 项目外包

我们也是接外包滴，如果你有项目想要外包，可以微信联系【**Aix9975**】。

团队包含专业的项目经理、架构师、前端工程师、后端工程师、测试工程师、运维工程师，可以提供全流程的外包服务。

项目可以是视频监控平台、IoT 物联网系统、MES 制造执行系统、WMS 仓库管理系统、PMS 项目管理系统、AI 智能体平台、数据大屏、支付系统、微信公众号等等。

## 🐼 内置功能

系统内置多种业务功能，可以用于快速搭建你的业务系统：

![功能分层](/.image/common/ruoyi-vue-pro-biz.png)

* 通用模块（必选）：系统功能、基础设施
* 通用模块（可选）：支付系统、数据报表、微信公众号
* 业务系统（按需）：视频监控系统 (Media / GB28181)、IoT 物联网系统、AI 大模型与智能体 (AI / Spring AI Agent)、MES 制造执行系统、WMS 仓库管理系统、PMS 项目管理系统、Mobile 手机移动端

> 友情提示：本项目基于 RuoYi-Vue 修改，**重构优化**后端的代码，**美化**前端的界面。
>
> * 额外新增的功能，我们使用 🚀 标记。
> * 重新实现的功能，我们使用 ⭐️ 标记。

🙂 所有功能，都通过 **单元测试** 保证高质量。

### 系统功能

|     | 功能    | 描述                              |
|-----|-------|---------------------------------|
|     | 用户管理  | 用户是系统操作者，该功能主要完成系统用户配置          |
| ⭐️  | 在线用户  | 当前系统中活跃用户状态监控，支持手动踢下线           |
|     | 角色管理  | 角色菜单权限分配、设置角色按机构进行数据范围权限划分      |
|     | 菜单管理  | 配置系统菜单、操作权限、按钮权限标识等，本地缓存提供性能    |
|     | 部门管理  | 配置系统组织机构（公司、部门、小组），树结构展现支持数据权限  |
|     | 岗位管理  | 配置系统用户所属担任职务                    |
| 🚀  | 租户管理  | 配置系统租户，支持 SaaS 场景下的多租户功能        |
| 🚀  | 租户套餐  | 配置租户套餐，自定每个租户的菜单、操作、按钮的权限       |
|     | 字典管理  | 对系统中经常使用的一些较为固定的数据进行维护          |
| 🚀  | 短信管理  | 短信渠道、短信模板、短信日志，对接阿里云、腾讯云等主流短信平台 |
| 🚀  | 邮件管理  | 邮箱账号、邮件模版、邮件发送日志，支持所有邮件平台       |
| 🚀  | 站内信   | 系统内的消息通知，提供站内信模版、站内信消息          |
| 🚀  | 操作日志  | 系统正常操作日志记录和查询，集成 Swagger 生成日志内容 |
| ⭐️  | 登录日志  | 系统登录日志记录查询，包含登录异常               |
| 🚀  | 错误码管理 | 系统所有错误码的管理，可在线修改错误提示，无需重启服务     |
|     | 通知公告  | 系统通知公告信息发布维护                    |
| 🚀  | 敏感词   | 配置系统敏感词，支持标签分组                  |
| 🚀  | 应用管理  | 管理 SSO 单点登录的应用，支持多种 OAuth2 授权方式 |
| 🚀  | 地区管理  | 展示省份、城市、区镇等城市信息，支持 IP 对应城市      |

![功能图](/.image/common/system-feature.png)

### 基础设施

|     | 功能        | 描述                                           |
|-----|-----------|----------------------------------------------|
| 🚀  | 代码生成      | 前后端代码的生成（Java、Vue、SQL、单元测试），支持 CRUD 下载       |
| 🚀  | 系统接口      | 基于 Swagger 自动生成相关的 RESTful API 接口文档          |
| 🚀  | 数据库文档     | 基于 Screw 自动生成数据库文档，支持导出 Word、HTML、MD 格式      |
|     | 表单构建      | 拖动表单元素生成相应的 HTML 代码，支持导出 JSON、Vue 文件         |
| 🚀  | 配置管理      | 对系统动态配置常用参数，支持 SpringBoot 加载                 |
| ⭐️  | 定时任务      | 在线（添加、修改、删除)任务调度包含执行结果日志                     |
| 🚀  | 文件服务      | 支持将文件存储到 S3（MinIO、阿里云、腾讯云、七牛云）、本地、FTP、数据库等   |
| 🚀  | WebSocket | 提供 WebSocket 接入示例，支持一对一、一对多发送方式              |
| 🚀  | API 日志    | 包括 RESTful API 访问日志、异常日志两部分，方便排查 API 相关的问题   |
|     | MySQL 监控  | 监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈              |
|     | Redis 监控  | 监控 Redis 数据库的使用情况，使用的 Redis Key 管理           |
| 🚀  | 消息队列      | 基于 Redis 实现消息队列，Stream 提供集群消费，Pub/Sub 提供广播消费 |
| 🚀  | Java 监控   | 基于 Spring Boot Admin 实现 Java 应用的监控           |
| 🚀  | 链路追踪      | 接入 SkyWalking 组件，实现链路追踪                      |
| 🚀  | 日志中心      | 接入 SkyWalking 组件，实现日志中心                      |
| 🚀  | 服务保障      | 基于 Redis 实现分布式锁、幂等、限流功能，满足高并发场景              |
| 🚀  | 日志服务      | 轻量级日志中心，查看远程服务器的日志                           |
| 🚀  | 单元测试      | 基于 JUnit + Mockito 实现单元测试，保证功能的正确性、代码的质量等    |

![功能图](/.image/common/infra-feature.png)

### 视频监控系统

深度融合 WVP-GB28181-Pro 视频监控能力与 ZLMediaKit 高性能流媒体服务器：

|     | 功能        | 描述                                                                                |
|-----|-----------|-----------------------------------------------------------------------------------|
| 🚀  | 国标设备接入 | 基于 JAIN-SIP 协议栈实现 GB28181-2016 标准设备注册、心跳保活、目录检索与通道同步                  |
| 🚀  | 流媒体集群   | 深度集成 ZLMediaKit 高性能流媒体服务器，支持流媒体节点集群管理与 Webhook 事件回调处理            |
| 🚀  | 实时视频点播 | 支持单路与多分屏实时视频播放，自适应 RTSP、RTMP、FLV、HLS、WebRTC 等多种流协议                   |
| 🚀  | 云台 PTZ 控制| 支持云台方向控制（上/下/左/右）、变焦、聚焦、光圈以及预置位设置、调用与巡航                        |
| 🚀  | 录像回放下载 | 支持国标设备端历史录像检索、点播回放与倍速播放，支持录像流下载保存                                |
| 🚀  | 云端录像计划 | 支持服务端录像计划配置、云端全天/报警录像存储、切片与时间轴回放                                   |
| 🚀  | 拉流代理推流 | 支持 RTSP/RTMP 等第三方流地址拉流代理转换，支持自定义推流管理与鉴权                               |
| 🚀  | 国标平台级联 | 支持作为下级向上级国标平台注册推送设备与通道，实现多平台跨网级联共享                              |
| 🚀  | 物联设备联动 | 接收 IoT 物联网联动事件（IotMediaLinkageEvent），快速响应预置位转向与应急录像                     |

### IoT 物联网

演示地址：<https://doc.iocoder.cn/iot/build>

![功能图](/.image/common/iot-feature.png)

![预览图](/.image/common/iot-preview.png)

|     | 功能        | 描述                                                                                |
|-----|-----------|-----------------------------------------------------------------------------------|
| 🚀  | 产品物模型   | 支持产品分类、物模型（属性、事件、服务）自定义与模板继承                                           |
| 🚀  | 设备管理     | 设备注册认证、生命周期管理、设备分组、子设备关联与实时状态监控                                    |
| 🚀  | 协议与编解码 | 支持 MQTT、Modbus 等多种协议接入，提供灵活的编解码脚本与点播调度                                  |
| 🚀  | 场景规则引擎 | 可视化规则引擎，支持触发条件、执行动作配置及告警通知                                              |
| 🚀  | 监控联动     | 物联网设备告警与状态变化联动视频监控，自动调用云台预置位、触发抓拍与视频录像                      |

### AI 大模型与智能体

演示地址：<https://doc.iocoder.cn/ai-preview/>

![功能图](/.image/common/ai-feature.png)

![功能图](/.image/common/ai-preview.gif)

|     | 功能        | 描述                                                                                |
|-----|-----------|-----------------------------------------------------------------------------------|
| 🚀  | 大模型对话   | 对接 OpenAI、通义千问、文心一言、DeepSeek、MiMo 等主流大模型，支持多轮对话与流式输出              |
| 🚀  | AI 绘画与多模态 | 支持 Midjourney、Stable Diffusion、DALL-E 等图像生成，支持图像理解与多模态视觉传参              |
| 🚀  | 音乐与思维导图 | 支持 Suno 音乐创作，支持一键生成思维导图并导出                                                    |
| 🚀  | Agent 智能体编排 | 基于 Spring AI 构建，组合模型、系统指令、技能与知识库，打造多场景专属 Agent                       |
| 🚀  | 工具与 MCP/SKILL | 支持 MCP 协议、SKILL 工具调用，让大模型具备主动获取信息和执行任务的能力                           |
| 🚀  | RAG 知识库检索 | 集成 Milvus 向量数据库，支持私有文档切片、向量化存储及对话精准增强检索                            |

### WMS 仓库管理系统

演示地址：<https://doc.iocoder.cn/wms-preview/>

![功能图](/.image/common/wms-feature.png)

![功能图](/.image/common/wms-preview.png)

### MES 制造执行系统

演示地址：<https://doc.iocoder.cn/mes-preview/>

![功能图](/.image/common/mes-feature.png)

![功能图](/.image/common/mes-preview.png)

### PMS 项目管理系统

演示地址：<https://doc.iocoder.cn/pms-preview/>

![功能图](/.image/common/pms-feature.png)

![预览图](/.image/common/pms-preview.png)

### 支付系统

|     | 功能   | 描述                        |
|-----|------|---------------------------|
| 🚀  | 应用信息 | 配置商户的应用信息，对接支付宝、微信等多个支付渠道 |
| 🚀  | 支付订单 | 查看用户发起的支付宝、微信等的【支付】订单     |
| 🚀  | 退款订单 | 查看用户发起的支付宝、微信等的【退款】订单     |
| 🚀  | 回调通知 | 查看支付回调业务的【支付】【退款】的通知结果    |
| 🚀  | 接入示例 | 提供接入支付系统的【支付】【退款】的功能实战    |

### 数据报表

|     | 功能    | 描述                 |
|-----|-------|--------------------|
| 🚀  | 报表设计器 | 支持数据报表、图形报表、打印设计等  |
| 🚀  | 大屏设计器 | 拖拽生成数据大屏，内置几十种图表组件 |

### 微信公众号

|    | 功能     | 描述                            |
|----|--------|-------------------------------|
| 🚀 | 账号管理   | 配置接入的微信公众号，可支持多个公众号           |
| 🚀 | 数据统计   | 统计公众号的用户增减、累计用户、消息概况、接口分析等数据  |
| 🚀 | 粉丝管理   | 查看已关注、取关的粉丝列表，可对粉丝进行同步、打标签等操作 |
| 🚀 | 消息管理   | 查看粉丝发送的消息列表，可主动回复粉丝消息         |
| 🚀 | 模版消息   | 配置和发送模版消息，用于向粉丝推送通知类消息        |
| 🚀 | 自动回复   | 自动回复粉丝发送的消息，支持关注回复、消息回复、关键字回复 |
| 🚀 | 标签管理   | 对公众号的标签进行创建、查询、修改、删除等操作       |
| 🚀 | 菜单管理   | 自定义公众号的菜单，也可以从公众号同步菜单         |
| 🚀 | 素材管理   | 管理公众号的图片、语音、视频等素材，支持在线播放语音、视频 |
| 🚀 | 图文草稿箱  | 新增常用的图文素材到草稿箱，可发布到公众号         |
| 🚀 | 图文发表记录 | 查看已发布成功的图文素材，支持删除操作           |

## 🐨 技术栈

### 模块

| 项目                    | 说明                                                     |
|-----------------------|--------------------------------------------------------|
| `yudao-dependencies`  | Maven 依赖版本管理                                       |
| `yudao-framework`     | Java 核心框架拓展（Web、Security、MyBatis、Redis、WebSocket 等） |
| `yudao-server`        | 管理后台服务端（Spring Boot 核心启动入口）                  |
| `yudao-module-system` | 系统管理 Module 模块（用户、角色、租户、权限、短信、日志等）      |
| `yudao-module-infra`  | 基础设施 Module 模块（代码生成、接口文档、定时任务、文件存储等）  |
| `yudao-module-media`  | 视频监控流媒体 Module 模块（WVP-GB28181 + ZLMediaKit）    |
| `yudao-module-iot`    | IoT 物联网 Module 模块（设备接入、物模型、规则引擎、监控联动）    |
| `yudao-module-ai`     | AI 大模型 Module 模块（对话、绘画、音乐、思维导图、多模态）      |
| `yudao-module-ai1-springai` | AI Agent 智能体编排 Module 模块（Spring AI、MCP、SKILL、RAG） |
| `yudao-module-pay`    | 支付系统 Module 模块（微信、支付宝支付与退款）               |
| `yudao-module-report` | 数据报表与大屏 Module 模块（积木报表、UReport2、GoView）    |
| `yudao-module-mp`     | 微信公众号 Module 模块                                   |
| `yudao-module-mes`    | MES 执行制造系统 Module 模块                             |
| `yudao-module-wms`    | WMS 仓库管理 Module 模块                                 |
| `yudao-module-pms`    | PMS 项目管理 Module 模块                                 |

### 框架

| 框架                                                                                          | 说明               | 版本             | 学习指南                                                           |
|---------------------------------------------------------------------------------------------|------------------|----------------|----------------------------------------------------------------|
| [Spring Boot](https://spring.io/projects/spring-boot)                                       | 应用开发框架           | 4.1.2          | [文档](https://github.com/YunaiV/SpringBoot-Labs)                |
| [MySQL](https://www.mysql.com/cn/)                                                          | 数据库服务器           | 5.7 / 8.0+     |                                                                |
| [Druid](https://github.com/alibaba/druid)                                                   | JDBC 连接池、监控组件    | 1.2.28         | [文档](http://www.iocoder.cn/Spring-Boot/datasource-pool/?yudao) |
| [MyBatis Plus](https://mp.baomidou.com/)                                                    | MyBatis 增强工具包    | 3.5.16         | [文档](http://www.iocoder.cn/Spring-Boot/MyBatis/?yudao)         |
| [Dynamic Datasource](https://dynamic-datasource.com/)                                       | 动态数据源            | 4.5.0          | [文档](http://www.iocoder.cn/Spring-Boot/datasource-pool/?yudao) |
| [Redis](https://redis.io/)                                                                  | key-value 数据库    | 5.0 / 6.0 /7.0 |                                                                |
| [Redisson](https://github.com/redisson/redisson)                                            | Redis 客户端        | 4.6.1          | [文档](http://www.iocoder.cn/Spring-Boot/Redis/?yudao)           |
| [Spring MVC](https://github.com/spring-projects/spring-framework/tree/master/spring-webmvc) | MVC 框架           | 7.0.8          | [文档](http://www.iocoder.cn/SpringMVC/MVC/?yudao)               |
| [Spring Security](https://github.com/spring-projects/spring-security)                       | Spring 安全框架      | 7.1.0          | [文档](http://www.iocoder.cn/Spring-Boot/Spring-Security/?yudao) |
| [Hibernate Validator](https://github.com/hibernate/hibernate-validator)                     | 参数校验组件           | 9.1.0          | [文档](http://www.iocoder.cn/Spring-Boot/Validation/?yudao)      |
| [Quartz](https://github.com/quartz-scheduler)                                               | 任务调度组件           | 2.5.2          | [文档](http://www.iocoder.cn/Spring-Boot/Job/?yudao)             |
| [Springdoc](https://springdoc.org/)                                                         | Swagger 文档       | 3.0.3          | [文档](http://www.iocoder.cn/Spring-Boot/Swagger/?yudao)         |
| [SkyWalking](https://skywalking.apache.org/)                                                | 分布式应用追踪系统        | 9.6.0          | [文档](http://www.iocoder.cn/Spring-Boot/SkyWalking/?yudao)      |
| [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin)                       | Spring Boot 监控平台 | 4.0.4          | [文档](http://www.iocoder.cn/Spring-Boot/Admin/?yudao)           |
| [ZLMediaKit](https://github.com/ZLMediaKit/ZLMediaKit)                                     | 高性能流媒体服务器        | -              | [文档](https://docs.zlmediakit.com/)                           |
| [JAIN-SIP](https://github.com/usnistgov/jsip)                                               | SIP 国标协议栈        | 1.2.327        | -                                                              |
| [Spring AI](https://spring.io/projects/spring-ai)                                           | AI Agent 智能体框架   | 1.1.0-M5       | [文档](https://docs.spring.io/spring-ai/reference/)              |
| [Jackson](https://github.com/FasterXML/jackson)                                             | JSON 工具库         | 3.1.4          |                                                                |
| [MapStruct](https://mapstruct.org/)                                                         | Java Bean 转换     | 1.7.0.Beta2    | [文档](http://www.iocoder.cn/Spring-Boot/MapStruct/?yudao)       |
| [Lombok](https://projectlombok.org/)                                                        | 消除冗长的 Java 代码    | 1.18.48        | [文档](http://www.iocoder.cn/Spring-Boot/Lombok/?yudao)          |
| [JUnit](https://junit.org/junit5/)                                                          | Java 单元测试框架      | 6.0.3          | -                                                              |
| [Mockito](https://github.com/mockito/mockito)                                               | Java Mock 框架     | 5.23.0         | -                                                              |

## 🐷 演示图

### 系统功能

| 模块       | biu                         | biu                       | biu                       |
|----------|-----------------------------|---------------------------|---------------------------|
| 登录 & 首页  | ![登录](/.image/登录.jpg)       | ![首页](/.image/首页.jpg)     | ![个人中心](/.image/个人中心.jpg) |
| 用户 & 应用  | ![用户管理](/.image/用户管理.jpg)   | ![令牌管理](/.image/令牌管理.jpg) | ![应用管理](/.image/应用管理.jpg) |
| 租户 & 套餐  | ![租户管理](/.image/租户管理.jpg)   | ![租户套餐](/.image/租户套餐.png) | -                         |
| 部门 & 岗位  | ![部门管理](/.image/部门管理.jpg)   | ![岗位管理](/.image/岗位管理.jpg) | -                         |
| 菜单 & 角色  | ![菜单管理](/.image/菜单管理.jpg)   | ![角色管理](/.image/角色管理.jpg) | -                         |
| 审计日志     | ![操作日志](/.image/操作日志.jpg)   | ![登录日志](/.image/登录日志.jpg) | -                         |
| 短信       | ![短信渠道](/.image/短信渠道.jpg)   | ![短信模板](/.image/短信模板.jpg) | ![短信日志](/.image/短信日志.jpg) |
| 字典 & 敏感词 | ![字典类型](/.image/字典类型.jpg)   | ![字典数据](/.image/字典数据.jpg) | ![敏感词](/.image/敏感词.jpg)   |
| 错误码 & 通知 | ![错误码管理](/.image/错误码管理.jpg) | ![通知公告](/.image/通知公告.jpg) | -                         |

### 基础设施

| 模块            | biu                           | biu                         | biu                       |
|---------------|-------------------------------|-----------------------------|---------------------------|
| 代码生成          | ![代码生成](/.image/代码生成.jpg)     | ![生成效果](/.image/生成效果.jpg)   | -                         |
| 文档            | ![系统接口](/.image/系统接口.jpg)     | ![数据库文档](/.image/数据库文档.jpg) | -                         |
| 文件 & 配置       | ![文件配置](/.image/文件配置.jpg)     | ![文件管理](/.image/文件管理2.jpg)  | ![配置管理](/.image/配置管理.jpg) |
| 定时任务          | ![定时任务](/.image/定时任务.jpg)     | ![任务日志](/.image/任务日志.jpg)   | -                         |
| API 日志        | ![访问日志](/.image/访问日志.jpg)     | ![错误日志](/.image/错误日志.jpg)   | -                         |
| MySQL & Redis | ![MySQL](/.image/MySQL.jpg)   | ![Redis](/.image/Redis.jpg) | -                         |
| 监控平台          | ![Java监控](/.image/Java监控.jpg) | ![链路追踪](/.image/链路追踪.jpg)   | ![日志中心](/.image/日志中心.jpg) |

### 支付系统

| 模块      | biu                       | biu                             | biu                             |
|---------|---------------------------|---------------------------------|---------------------------------|
| 商家 & 应用 | ![商户信息](/.image/商户信息.jpg) | ![应用信息-列表](/.image/应用信息-列表.jpg) | ![应用信息-编辑](/.image/应用信息-编辑.jpg) |
| 支付 & 退款 | ![支付订单](/.image/支付订单.jpg) | ![退款订单](/.image/退款订单.jpg)       | ---                             |

### 数据报表

| 模块    | biu                             | biu                             | biu                                   |
|-------|---------------------------------|---------------------------------|---------------------------------------|
| 报表设计器 | ![数据报表](/.image/报表设计器-数据报表.jpg) | ![图形报表](/.image/报表设计器-图形报表.jpg) | ![报表设计器-打印设计](/.image/报表设计器-打印设计.jpg) |
| 大屏设计器 | ![大屏列表](/.image/大屏设计器-列表.jpg)   | ![大屏预览](/.image/大屏设计器-预览.jpg)   | ![大屏编辑](/.image/大屏设计器-编辑.jpg)         |

### 移动端（管理后台）

| biu                              | biu                              | biu                              |
|----------------------------------|----------------------------------|----------------------------------|
| ![](/.image/admin-uniapp/01.png) | ![](/.image/admin-uniapp/02.png) | ![](/.image/admin-uniapp/03.png) |
| ![](/.image/admin-uniapp/04.png) | ![](/.image/admin-uniapp/05.png) | ![](/.image/admin-uniapp/06.png) |
| ![](/.image/admin-uniapp/07.png) | ![](/.image/admin-uniapp/08.png) | ![](/.image/admin-uniapp/09.png) |

目前已经实现登录、我的、工作台、编辑资料、头像修改、密码修改、常见问题、关于我们等基础功能。
