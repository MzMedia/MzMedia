package com.mz.iot.service;

import com.mz.iot.domain.DeviceUser;
import com.mz.iot.domain.vo.DeviceUserVo;
import com.mz.iot.domain.bo.DeviceUserBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备用户Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IDeviceUserService {

    /**
     * 查询设备用户
     */
    DeviceUserVo queryById(Long userId);

    /**
     * 查询设备用户列表
     */
    TableDataInfo<DeviceUserVo> queryPageList(DeviceUserBo bo, PageQuery pageQuery);

    /**
     * 查询设备用户列表
     */
    List<DeviceUserVo> queryList(DeviceUserBo bo);

    /**
     * 新增设备用户
     */
    Boolean insertByBo(DeviceUserBo bo);

    /**
     * 修改设备用户
     */
    Boolean updateByBo(DeviceUserBo bo);

    /**
     * 校验并批量删除设备用户信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
