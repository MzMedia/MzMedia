package com.mz.iot.service;

import com.mz.iot.domain.DeviceGroup;
import com.mz.iot.domain.vo.DeviceGroupVo;
import com.mz.iot.domain.bo.DeviceGroupBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备分组Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IDeviceGroupService {

    /**
     * 查询设备分组
     */
    DeviceGroupVo queryById(Long deviceId);

    /**
     * 查询设备分组列表
     */
    TableDataInfo<DeviceGroupVo> queryPageList(DeviceGroupBo bo, PageQuery pageQuery);

    /**
     * 查询设备分组列表
     */
    List<DeviceGroupVo> queryList(DeviceGroupBo bo);

    /**
     * 新增设备分组
     */
    Boolean insertByBo(DeviceGroupBo bo);

    /**
     * 修改设备分组
     */
    Boolean updateByBo(DeviceGroupBo bo);

    /**
     * 校验并批量删除设备分组信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
