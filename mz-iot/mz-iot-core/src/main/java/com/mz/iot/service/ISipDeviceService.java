package com.mz.iot.service;

import com.mz.iot.domain.SipDevice;
import com.mz.iot.domain.vo.SipDeviceVo;
import com.mz.iot.domain.bo.SipDeviceBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 监控设备Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface ISipDeviceService {

    /**
     * 查询监控设备
     */
    SipDeviceVo queryById(Long deviceId);

    /**
     * 查询监控设备列表
     */
    TableDataInfo<SipDeviceVo> queryPageList(SipDeviceBo bo, PageQuery pageQuery);

    /**
     * 查询监控设备列表
     */
    List<SipDeviceVo> queryList(SipDeviceBo bo);

    /**
     * 新增监控设备
     */
    Boolean insertByBo(SipDeviceBo bo);

    /**
     * 修改监控设备
     */
    Boolean updateByBo(SipDeviceBo bo);

    /**
     * 校验并批量删除监控设备信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
