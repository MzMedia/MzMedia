
<div style="height: 10px; clear: both;"></div>

- - -
### 一、平台简介
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://gitee.com/dromara/RuoYi-Vue-Plus/blob/master/LICENSE)
[![使用IntelliJ IDEA开发维护](https://img.shields.io/badge/IntelliJ%20IDEA-提供支持-blue.svg)](https://www.jetbrains.com/?from=RuoYi-Vue-Plus)
<br>
[![RuoYi-Vue-Plus](https://img.shields.io/badge/RuoYi_Vue_Plus-5.1.0-success.svg)](https://gitee.com/dromara/RuoYi-Vue-Plus)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-blue.svg)]()
[![JDK-17](https://img.shields.io/badge/JDK-17-green.svg)]()
[![JDK-19](https://img.shields.io/badge/JDK-19-green.svg)]()

1. MzMedia开源视频联动物联网平台，简单易用，更适合中小企业和个人学习使用。适用于智能家居、智慧社区、农业监测、水利监测、工业控制等。

2. 系统后端采用Spring boot；前端采用Vue3+Element Plus；消息服务器采用EMQX5.0；数据库采用Mysql、TDengine和Redis；设备端支持OpenWrt,FreeRTOS,RT-Thread等操作系统的接入sdk；


> RuoYi-Vue-Plus 是重写 RuoYi-Vue 针对 `分布式集群与多租户` 场景全方位升级

> 项目代码、文档 均开源免费可商用 遵循开源协议在项目中保留开源协议文件即可<br>
活到老写到老 为兴趣而开源 为学习而开源 为让大家真正可以学到技术而开源

> 系统演示: [传送门](https://plus-doc.dromara.org/#/common/demo_system)

> 文档地址: [plus-doc](https://plus-doc.dromara.org) 

### 二、系统功能

| 功能          | 本项目                                                                                                              | 
|-------------|-------------------------------------------------------------------------------------------------------------------|
| 前端项目        | 采用 Vue3 + TS + ElementPlus 重写                                                                                     | 
| 后端项目结构      | 采用插件化 + 扩展包形式 结构解耦 易于扩展                                                                                           | 
| 后端代码风格      | 严格遵守Alibaba规范与项目统一配置的代码格式化                                                                                        | 
| Web容器       | 采用 Undertow 基于 XNIO 的高性能容器                                                                                        | 
| 权限认证        | 采用 Sa-Token、Jwt 静态使用功能齐全 低耦合 高扩展                                                                                  | 
| 权限注解        | 采用 Sa-Token 支持注解 登录校验、角色校验、权限校验、二级认证校验、HttpBasic校验、忽略校验<br/>角色与权限校验支持多种条件 如 `AND` `OR` 或 `权限 OR 角色` 等复杂表达式        | 
| 三方鉴权        | 采用 JustAuth 第三方登录组件 支持微信、钉钉等数十种三方认证                                                                               | 
| 关系数据库支持     | 原生支持 MySQL、Oracle、PostgreSQL、SQLServer<br/>可同时使用异构切换                                                              | 
| 缓存数据库       | 支持 Redis 5-7 支持大部分新功能特性 如 分布式限流、分布式队列                                                                             | 
| Redis客户端    | 采用 Redisson Redis官方推荐 基于Netty的客户端工具<br/>支持Redis 90%以上的命令 底层优化规避很多不正确的用法 例如: keys被转换为scan<br/>支持单机、哨兵、单主集群、多主集群等模式 | 
| 缓存注解        | 采用 Spring-Cache 注解 对其扩展了实现支持了更多功能<br/>例如 过期时间 最大空闲时间 组最大长度等 只需一个注解即可完成数据自动缓存                                      | 
| ORM框架       | 采用 Mybatis-Plus 基于对象几乎不用写SQL全java操作 功能强大插件众多<br/>例如多租户插件 分页插件 乐观锁插件等等                                             | 
| SQL监控       | 采用 p6spy 可输出完整SQL与执行时间监控                                                                                          |
| 数据分页        | 采用 Mybatis-Plus 分页插件<br/>框架对其进行了扩展 对象化分页对象 支持多种方式传参 支持前端多排序 复杂排序                                                  |
| 数据权限        | 采用 Mybatis-Plus 插件 自行分析拼接SQL 无感式过滤<br/>只需为Mapper设置好注解条件 支持多种自定义 不限于部门角色                                           |
| 数据脱敏        | 采用 注解 + jackson 序列化期间脱敏 支持不同模块不同的脱敏条件<br/>支持多种策略 如身份证、手机号、地址、邮箱、银行卡等 可自行扩展                                        | 
| 数据加解密       | 采用 注解 + mybatis 拦截器 对存取数据期间自动加解密<br/>支持多种策略 如BASE64、AES、RSA、SM2、SM4等                                              | 
| 接口传输加密      | 采用 动态 AES + RSA 加密请求 body 每一次请求秘钥都不同大幅度降低可破解性                                                                     | 
| 数据翻译        | 采用 注解 + jackson 序列化期间动态修改数据 数据进行翻译<br/>支持多种模式: `映射翻译` `直接翻译` `其他扩展条件翻译` 接口化两步即可完成自定义扩展 内置多种翻译实现                   | 
| 多数据源框架      | 采用 dynamic-datasource 支持世面大部分数据库<br/>通过yml配置即可动态管理异构不同种类的数据库 也可通过前端页面添加数据源<br/>支持spel表达式从请求头参数等条件切换数据源            | 
| 多数据源事务      | 采用 dynamic-datasource 支持多数据源不同种类的数据库事务回滚                                                                          | 
| 数据库连接池      | 采用 HikariCP Spring官方内置连接池 配置简单 以性能与稳定性闻名天下                                                                        | 
| 数据库主键       | 采用 雪花ID 基于时间戳的 有序增长 唯一ID 再也不用为分库分表 数据合并主键冲突重复而发愁                                                                  | 
| WebSocket协议 | 基于 Spring 封装的 WebSocket 协议 扩展了Token鉴权与分布式会话同步 不再只是基于单机的废物                                                         | 
| 序列化         | 采用 Jackson Spring官方内置序列化 靠谱!!!                                                                                    | 
| 分布式幂等       | 参考美团GTIS防重系统简化实现(细节可看文档)                                                                                          | 
| 分布式锁        | 采用 Lock4j 底层基于 Redisson                                                                                           |
| 分布式任务调度     | 采用 PowerJob 天生支持分布式 统一的管理中心                                                                                       | 
| 文件存储        | 采用 Minio 分布式文件存储 天生支持多机、多硬盘、多分片、多副本存储<br/>支持权限管理 安全可靠 文件可加密存储                                                     | 
| 云存储         | 采用 AWS S3 协议客户端 支持 七牛、阿里、腾讯 等一切支持S3协议的厂家                                                                          | 
| 短信          | 采用 sms4j 短信融合包 支持数十种短信厂家 只需在yml配置好厂家密钥即可使用 可多厂家共用                                                                 |
| 邮件          | 采用 mail-api 通用协议支持大部分邮件厂商                                                                                         | 
| 接口文档        | 采用 SpringDoc、javadoc 无注解零入侵基于java注释<br/>只需把注释写好 无需再写一大堆的文档注解了                                                     | 
| 校验框架        | 采用 Validation 支持注解与工具类校验 注解支持国际化                                                                                  | 
| Excel框架     | 采用 Alibaba EasyExcel 基于插件化<br/>框架对其增加了很多功能 例如 自动合并相同内容 自动排列布局 字典翻译等                                               | 
| 工具类框架       | 采用 Hutool、Lombok 上百种工具覆盖90%的使用需求 基于注解自动生成 get set 等简化框架大量代码                                                       | 
| 监控框架        | 采用 SpringBoot-Admin 基于SpringBoot官方 actuator 探针机制<br/>实时监控服务状态 框架还为其扩展了在线日志查看监控                                    | 
| 链路追踪        | 采用 Apache SkyWalking 还在为请求不知道去哪了 到哪出了问题而烦恼吗<br/>用了它即可实时查看请求经过的每一处每一个节点                                            | 
| 代码生成器       | 只需设计好表结构 一键生成所有crud代码与页面<br/>降低80%的开发量 把精力都投入到业务设计上<br/>框架为其适配MP、SpringDoc规范化代码 同时支持动态多数据源代码生成                    | 
| 部署方式        | 支持 Docker 编排 一键搭建所有环境 让开发人员从此不再为搭建环境而烦恼                                                                           | 
| 项目路径修改      | 提供详细的修改方案文档 并为其做了一些改动 非常简单即可修改成自己想要的                                                                              | 
| 国际化         | 基于请求头动态返回不同语种的文本内容 开发难度低 有对应的工具类 支持大部分注解内容国际化                                                                     | 
| 代码单例测试      | 提供单例测试 使用方式编写方法与maven多环境单测插件                                                                                      | 
| Demo案例      | 提供框架功能的实际使用案例 单独一个模块提供了很多很全                                                                                       | 

### 三、技术栈
* 服务端
    - 相关技术：Spring boot、Mybatis-Plus、Sa-Token、Jwt、Mysql、Redis、TDengine、EMQX5.0、Vertx等
* Web端
    - 相关技术：Vue3、TS、Element Plus、Vite等
* 可视化大屏(GoView)
    - 相关技术：Vue、TypeScript4、Vite、NaiveUI、ECharts、Pinia等
* 组态(ShadowEditor)
    - 相关技术：Vue、ElementUI、Three.js、Go、MongoDB等
* 规则引擎（node-red+liteflow）
    - 相关技术：nodejs、html、js等
* 流媒体服务器（zlmediakit）
    - 相关技术：c++、sip、gb28181,rtmp,rtsp,flv,ffmpeg,ffprobe等
* 硬件端
    - 相关技术：ESP-IDF、Arduino、openwrt,freeos,rt-thread等

### 四、本项目业务特性

| 业务     | 功能说明                                                                 | 本项目 |
|--------|----------------------------------------------------------------------|-----|
| 租户管理   | 系统内租户的管理 如:租户套餐、过期时间、用户数量、企业信息等                                      | 支持  |
| 租户套餐管理 | 系统内租户所能使用的套餐管理 如:套餐内所包含的菜单等                                          | 支持  | 
| 客户端管理  | 系统内对接的所有客户端管理 如: pc端、小程序端等<br>支持动态授权登录方式 如: 短信登录、密码登录等 支持动态控制token时效 | 支持  | 
| 用户管理   | 用户的管理配置 如:新增用户、分配用户所属部门、角色、岗位等                                       | 支持  |
| 部门管理   | 配置系统组织机构（公司、部门、小组） 树结构展现支持数据权限                                       | 支持  |
| 岗位管理   | 配置系统用户所属担任职务                                                         | 支持  | 
| 菜单管理   | 配置系统菜单、操作权限、按钮权限标识等                                                  | 支持  |
| 角色管理   | 角色菜单权限分配、设置角色按机构进行数据范围权限划分                                           | 支持  | 
| 字典管理   | 对系统中经常使用的一些较为固定的数据进行维护                                               | 支持  | 
| 参数管理   | 对系统动态配置常用参数                                                          | 支持  | 
| 通知公告   | 系统通知公告信息发布维护                                                         | 支持  | 
| 操作日志   | 系统正常操作日志记录和查询 系统异常信息日志记录和查询                                          | 支持  | 
| 登录日志   | 系统登录日志记录查询包含登录异常                                                     | 支持  | 
| 文件管理   | 系统文件展示、上传、下载、删除等管理                                                   | 支持  | 
| 文件配置管理 | 系统文件上传、下载所需要的配置信息动态添加、修改、删除等管理                                       | 支持  | 
| 在线用户管理 | 已登录系统的在线用户信息监控与强制踢出操作                                                | 支持  |
| 定时任务   | 运行报表、任务管理(添加、修改、删除)、日志管理、执行器管理等                                      | 支持  | 
| 代码生成   | 多数据源前后端代码的生成（java、html、xml、sql）支持CRUD下载                              | 支持  | 
| 系统接口   | 根据业务代码自动生成相关的api接口文档                                                 | 支持  | 
| 服务监控   | 监视集群系统CPU、内存、磁盘、堆栈、在线日志、Spring相关配置等                                  | 支持  | 
| 缓存监控   | 对系统的缓存信息查询，命令统计等。                                                    | 支持  | 
| 在线构建器  | 拖动表单元素生成相应的HTML代码。                                                   | 支持  | 
| 使用案例   | 系统的一些功能案例                                                            | 支持  | 

### 五、项目目录
> ui --------------------------- 前端项目<br/>
> devsdk ----------------------- 设备端sdk<br />
> script ----------------------- docker部署脚本和sql数据库脚本<br />

### 六、商用授权
项目采用AGPL3协议，可用于个人学习和使用，商业用途需要赞助项目，获得授权，并提供商业版本源码、可视化平台和移动端源码。赞助过的用户请下载商业版本源码。

### 七、参考文档

使用本项目前请仔细阅读文档重点注意事项
<br>
>[部署项目 必看](https://plus-doc.dromara.org/#/ruoyi-vue-plus/quickstart/deploy)
>>[https://plus-doc.dromara.org/#/ruoyi-vue-plus/quickstart/deploy](https://plus-doc.dromara.org/#/ruoyi-vue-plus/quickstart/deploy)
> 
>[参考文档 Wiki](https://plus-doc.dromara.org)
>>[https://plus-doc.dromara.org](https://plus-doc.dromara.org)


### 八、其他

* 定期同步升级 RuoYi-Vue-Plus 有用的更新


### 九、演示图例

|                                                                                            |                                                                                            |
|--------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------|
| ![输入图片说明](https://foruda.gitee.com/images/1680077524361362822/270bb429_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680077619939771291/989bf9b6_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680077681751513929/1c27c5bd_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680077721559267315/74d63e23_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680077765638904515/1b75d4a6_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078026375951297/eded7a4b_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078237104531207/0eb1b6a7_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078254306078709/5931e22f_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078287971528493/0b9af60a_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078308138770249/8d3b6696_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078352553634393/db5ef880_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078378238393374/601e4357_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078414983206024/2aae27c1_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078446738419874/ecce7d59_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078475971341775/149e8634_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078491666717143/3fadece7_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078558863188826/fb8ced2a_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078574561685461/ae68a0b2_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078594932772013/9d8bfec6_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078626493093532/fcfe4ff6_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078643608812515/0295bd4f_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078685196286463/d7612c81_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078703877318597/56fce0bc_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078716586545643/b6dbd68f_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078734103217688/eb1e6aa6_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078759131415480/73c525d8_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078779416197879/75e3ed02_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078802329118061/77e10915_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078893627848351/34a1c342_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078928175016986/f126ec4a_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078941718318363/b68a0f72_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680078963175518631/3bb769a1_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680078982294090567/b31c343d_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680079000642440444/77ca82a9_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680079020995074177/03b7d52e_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680079039367822173/76811806_1766278.png "屏幕截图") |
| ![输入图片说明](https://foruda.gitee.com/images/1680079274333484664/4dfdc7c0_1766278.png "屏幕截图") | ![输入图片说明](https://foruda.gitee.com/images/1680079290467458224/d6715fcf_1766278.png "屏幕截图") |












