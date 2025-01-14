package org.dromara.iot.domain.vo;

import org.dromara.iot.domain.Product;
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
 * 产品视图对象 product
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Product.class)
public class ProductVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 产品ID
     */
    @ExcelProperty(value = "产品ID")
    private Long id;

    /**
     * 产品分类ID
     */
    @ExcelProperty(value = "产品分类ID")
    private Long categoryId;

    /**
     * 产品名称
     */
    @ExcelProperty(value = "产品名称")
    private String productName;

    /**
     * 协议编号
     */
    @ExcelProperty(value = "协议编号")
    private String protocolCode;

    /**
     * 是否系统通用（0-否，1-是）
     */
    @ExcelProperty(value = "是否系统通用", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-否，1-是")
    private Long isSys;

    /**
     * 是否启用授权码（0-否，1-是）
     */
    @ExcelProperty(value = "是否启用授权码", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=-否，1-是")
    private Long isAuthorize;

    /**
     * mqtt账号
     */
    @ExcelProperty(value = "mqtt账号")
    private String mqttAccount;

    /**
     * mqtt密码
     */
    @ExcelProperty(value = "mqtt密码")
    private String mqttPassword;

    /**
     * 产品秘钥
     */
    @ExcelProperty(value = "产品秘钥")
    private String mqttSecret;

    /**
     * 状态（1-未发布，2-已发布）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-未发布，2-已发布")
    private Long status;

    /**
     * 物模型JSON（属性、功能、事件）
     */
    @ExcelProperty(value = "物模型JSON", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "属=性、功能、事件")
    private String thingsModelsJson;

    /**
     * 设备类型（1-直连设备、2-网关设备、3-监控设备）
     */
    @ExcelProperty(value = "设备类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-直连设备、2-网关设备、3-监控设备")
    private Long deviceType;

    /**
     * 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他）
     */
    @ExcelProperty(value = "联网方式", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1==wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他")
    private Long networkMethod;

    /**
     * 认证方式（1-简单认证、2-加密认证、3-简单+加密）
     */
    @ExcelProperty(value = "认证方式", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "1=-简单认证、2-加密认证、3-简单+加密")
    private Long vertificateMethod;

    /**
     * 图片地址
     */
    @ExcelProperty(value = "图片地址")
    private String imgUrl;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
