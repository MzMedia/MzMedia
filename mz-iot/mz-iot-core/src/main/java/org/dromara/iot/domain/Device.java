package org.dromara.iot.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 设备信息对象 device
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("device")
public class Device extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 设备ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 子用户ID
     */
    private Long subUid;

    /**
     * 子设备ID
     */
    private Long subDid;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备昵称
     */
    private String nickName;

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 设备编号
     */
    private String serialNumber;

    /**
     * 产品密钥
     */
    private String secret;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 设备位置
     */
    private String locate;

    /**
     * 设备标签
     */
    private String tag;

    /**
     * 设备状态（1-未激活，2-禁用，3-在线，4-离线）
     */
    private Long status;

    /**
     * 设备IP
     */
    private String ip;

    /**
     * 激活时间
     */
    private Date activeTime;

    /**
     * 物模型值
     */
    private String thingsModel;

    /**
     * 设备摘要
     */
    private String summary;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 备注
     */
    private String remark;


}
