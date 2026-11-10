/**
 * 代码生成模板类型
 */
export const InfraCodegenTemplateTypeEnum = {
  CRUD: 1, // 基础 CRUD
  TREE: 2, // 树形 CRUD
  SUB: 15, // 主子表 CRUD
}

/**
 * 任务状态的枚举
 */
export const InfraJobStatusEnum = {
  INIT: 0, // 初始化中
  NORMAL: 1, // 运行中
  STOP: 2, // 暂停运行
}

/**
 * API 异常数据的处理状态
 */
export const InfraApiErrorLogProcessStatusEnum = {
  INIT: 0, // 未处理
  DONE: 1, // 已处理
  IGNORE: 2, // 已忽略
}

/**
 * 文件存储器枚举
 */
export const InfraFileStorageEnum = {
  DB: 1, // 数据库
  LOCAL: 10, // 本地磁盘
  FTP: 11, // FTP 服务器
  SFTP: 12, // SFTP 服务器
  S3: 20, // S3 对象存储
}
