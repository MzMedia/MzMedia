package com.mz.iot.service;

import com.mz.iot.domain.DeviceLog;
import com.mz.iot.domain.vo.DeviceLogVo;
import com.mz.iot.domain.bo.DeviceLogBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备日志Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IDeviceLogService {

    /**
     * 查询设备日志
     */
    DeviceLogVo queryById(Long logId);

    /**
     * 查询设备日志列表
     */
    TableDataInfo<DeviceLogVo> queryPageList(DeviceLogBo bo, PageQuery pageQuery);

    /**
     * 查询设备日志列表
     */
    List<DeviceLogVo> queryList(DeviceLogBo bo);

    /**
     * 新增设备日志
     */
    Boolean insertByBo(DeviceLogBo bo);

    /**
     * 修改设备日志
     */
    Boolean updateByBo(DeviceLogBo bo);

    /**
     * 校验并批量删除设备日志信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
