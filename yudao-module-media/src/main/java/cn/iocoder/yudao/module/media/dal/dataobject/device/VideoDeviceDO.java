package cn.iocoder.yudao.module.media.dal.dataobject.device;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频监控 - 接入设备实体类 (GB28181及其他协议设备)
 *
 * @author MzMedia Architect
 */
@TableName("mz_video_device")
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoDeviceDO extends TenantBaseDO {

    /**
     * 自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 设备国标编号 (20位国标编码)
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 自定义名称
     */
    private String customName;

    /**
     * 设备生产厂商
     */
    private String manufacturer;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 固件版本
     */
    private String firmware;

    /**
     * 信令传输协议 (UDP/TCP)
     */
    private String transport;

    /**
     * 媒体流传输模式 (UDP / TCP-ACTIVE / TCP-PASSIVE)
     */
    private String streamMode;

    /**
     * 在线状态 (0:离线, 1:在线)
     */
    private Integer onLine;

    /**
     * 注册时间
     */
    private String registerTime;

    /**
     * 最近心跳保活时间
     */
    private String keepaliveTime;

    /**
     * 设备外网/局域网 IP
     */
    private String ip;

    /**
     * 设备 SIP 端口
     */
    private Integer port;

    /**
     * 注册有效时间 (秒)
     */
    private Integer expires;

    /**
     * 字符集 (GB2312 / UTF-8)
     */
    private String charset;

    /**
     * 宿主地址 (IP:Port)
     */
    private String hostAddress;

    /**
     * SDP媒体协商IP
     */
    private String sdpIp;

    /**
     * 本地监听IP
     */
    private String localIp;

    /**
     * SIP认证密码
     */
    private String password;

    /**
     * 指定关联的流媒体服务节点ID
     */
    private String mediaServerId;

    /**
     * 服务节点ID
     */
    private String serverId;

    /**
     * 心跳周期 (秒)
     */
    private Integer heartBeatInterval;

    /**
     * 心跳超时次数
     */
    private Integer heartBeatCount;
}
