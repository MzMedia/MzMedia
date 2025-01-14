package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.DeviceUser;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 设备用户视图对象 device_user
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = DeviceUser.class)
public class DeviceUserVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @ExcelProperty(value = "ID")
    private Long id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 用户昵称
     */
    @ExcelProperty(value = "用户昵称")
    private String userName;

    /**
     * 设备ID
     */
    @ExcelProperty(value = "设备ID")
    private Long deviceId;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码")
    private String phonenumber;

    /**
     * 是否为设备所有者（0=否，1=是）
     */
    @ExcelProperty(value = "是否为设备所有者", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0==否，1=是")
    private Long isOwner;

    /**
     * 用户物模型权限，多个以英文逗号分隔
     */
    @ExcelProperty(value = "用户物模型权限，多个以英文逗号分隔")
    private String perms;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
