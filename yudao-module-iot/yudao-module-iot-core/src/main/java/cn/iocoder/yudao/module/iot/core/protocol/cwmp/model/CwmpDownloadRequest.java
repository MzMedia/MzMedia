package cn.iocoder.yudao.module.iot.core.protocol.cwmp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TR-069 Download 固件/配置文件下载请求对象
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CwmpDownloadRequest {

    /**
     * 命令标识 Key
     */
    private String commandKey;

    /**
     * 文件类型，如 "1 Firmware Upgrade Image" 或 "3 Vendor Configuration File"
     */
    private String fileType;

    /**
     * 下载 URL
     */
    private String url;

    /**
     * 认证用户名
     */
    private String username;

    /**
     * 认证密码
     */
    private String password;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 目标文件名
     */
    private String targetFileName;

    /**
     * 延迟执行秒数
     */
    private Integer delaySeconds;

    /**
     * 成功回调 URL
     */
    private String successUrl;

    /**
     * 失败回调 URL
     */
    private String failureUrl;

}
