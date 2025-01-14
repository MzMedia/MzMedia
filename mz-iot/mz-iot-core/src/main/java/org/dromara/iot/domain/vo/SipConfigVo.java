package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.SipConfig;
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
 * sip系统配置视图对象 sip_config
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SipConfig.class)
public class SipConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键")
    private Long id;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID")
    private Long productId;

    /**
     * 使能开关
     */
    @ExcelProperty(value = "使能开关")
    private Long enabled;

    /**
     * 系统默认配置
     */
    @ExcelProperty(value = "系统默认配置")
    private Long isdefault;

    /**
     * 拓展sdp
     */
    @ExcelProperty(value = "拓展sdp")
    private Long seniorSdp;

    /**
     * 服务器域
     */
    @ExcelProperty(value = "服务器域")
    private String domain;

    /**
     * 服务器sipid
     */
    @ExcelProperty(value = "服务器sipid")
    private String sipid;

    /**
     * sip认证密码
     */
    @ExcelProperty(value = "sip认证密码")
    private String password;

    /**
     * sip接入IP
     */
    @ExcelProperty(value = "sip接入IP")
    private String ip;

    /**
     * sip接入端口号
     */
    @ExcelProperty(value = "sip接入端口号")
    private Long port;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
