package com.mz.iot.service;

import com.mz.iot.domain.Device;
import com.mz.iot.domain.vo.DeviceVo;
import com.mz.iot.domain.bo.DeviceBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备信息Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IDeviceService {

    /**
     * 查询设备信息
     */
    DeviceVo queryById(Long userId);

    /**
     * 查询设备信息列表
     */
    TableDataInfo<DeviceVo> queryPageList(DeviceBo bo, PageQuery pageQuery);

    /**
     * 查询设备信息列表
     */
    List<DeviceVo> queryList(DeviceBo bo);

    /**
     * 新增设备信息
     */
    Boolean insertByBo(DeviceBo bo);

    /**
     * 修改设备信息
     */
    Boolean updateByBo(DeviceBo bo);

    /**
     * 校验并批量删除设备信息信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
