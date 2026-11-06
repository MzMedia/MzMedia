export interface MediaServerVO {
  /**
   * 流媒体配置ID
   */
  id: string | number;

  /**
   * 服务器标识
   */
  serverId: string | number;

  /**
   * 使能开关
   */
  enabled: number;

  /**
   * 默认播放协议
   */
  protocol: string;

  /**
   * 服务器ip
   */
  ip: string;

  /**
   * 服务器域名
   */
  domain: string;

  /**
   * 回调服务器地址
   */
  hookurl: string;

  /**
   * 流媒体密钥
   */
  secret: string;

  /**
   * http端口
   */
  portHttp: number;

  /**
   * https端口
   */
  portHttps: number;

  /**
   * rtmp端口
   */
  portRtmp: number;

  /**
   * rtsp端口
   */
  portRtsp: number;

  /**
   * RTP收流端口
   */
  rtpProxyPort: number;

  /**
   * ws端口
   */
  portWs: number;

  /**
   * 是否使用多端口模式
   */
  rtpEnable: number;

  /**
   * rtp端口范围
   */
  rtpPortRange: string;

  /**
   * 录像服务端口
   */
  recordPort: number;

  /**
   * 是否自动同步配置ZLM
   */
  autoConfig: number;

  /**
   * 状态
   */
  status: number;

  /**
   * 备注
   */
  remark: string;

}

export interface MediaServerForm extends BaseEntity {
  /**
   * 流媒体配置ID
   */
  id?: string | number;

  /**
   * 服务器标识
   */
  serverId?: string | number;

  /**
   * 使能开关
   */
  enabled?: number;

  /**
   * 默认播放协议
   */
  protocol?: string;

  /**
   * 服务器ip
   */
  ip?: string;

  /**
   * 服务器域名
   */
  domain?: string;

  /**
   * 回调服务器地址
   */
  hookurl?: string;

  /**
   * 流媒体密钥
   */
  secret?: string;

  /**
   * http端口
   */
  portHttp?: number;

  /**
   * https端口
   */
  portHttps?: number;

  /**
   * rtmp端口
   */
  portRtmp?: number;

  /**
   * rtsp端口
   */
  portRtsp?: number;

  /**
   * RTP收流端口
   */
  rtpProxyPort?: number;

  /**
   * ws端口
   */
  portWs?: number;

  /**
   * 是否使用多端口模式
   */
  rtpEnable?: number;

  /**
   * rtp端口范围
   */
  rtpPortRange?: string;

  /**
   * 录像服务端口
   */
  recordPort?: number;

  /**
   * 是否自动同步配置ZLM
   */
  autoConfig?: number;

  /**
   * 状态
   */
  status?: number;

  /**
   * 备注
   */
  remark?: string;

}

export interface MediaServerQuery extends PageQuery {

  /**
   * 服务器标识
   */
  serverId?: string | number;

  /**
   * 使能开关
   */
  enabled?: number;

  /**
   * 默认播放协议
   */
  protocol?: string;

  /**
   * 服务器ip
   */
  ip?: string;

  /**
   * 服务器域名
   */
  domain?: string;

  /**
   * 回调服务器地址
   */
  hookurl?: string;

  /**
   * 流媒体密钥
   */
  secret?: string;

  /**
   * http端口
   */
  portHttp?: number;

  /**
   * https端口
   */
  portHttps?: number;

  /**
   * rtmp端口
   */
  portRtmp?: number;

  /**
   * rtsp端口
   */
  portRtsp?: number;

  /**
   * RTP收流端口
   */
  rtpProxyPort?: number;

  /**
   * ws端口
   */
  portWs?: number;

  /**
   * 是否使用多端口模式
   */
  rtpEnable?: number;

  /**
   * rtp端口范围
   */
  rtpPortRange?: string;

  /**
   * 录像服务端口
   */
  recordPort?: number;

  /**
   * 是否自动同步配置ZLM
   */
  autoConfig?: number;

  /**
   * 状态
   */
  status?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



