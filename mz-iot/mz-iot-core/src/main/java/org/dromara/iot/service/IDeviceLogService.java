package org.dromara.iot.service;

import org.dromara.iot.domain.vo.DeviceLogVo;
import org.dromara.iot.domain.bo.DeviceLogBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备日志Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IDeviceLogService {

    /**
     * 查询设备日志
     *
     * @param id 主键
     * @return 设备日志
     */
    DeviceLogVo queryById(Long id);

    /**
     * 分页查询设备日志列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备日志分页列表
     */
    TableDataInfo<DeviceLogVo> queryPageList(DeviceLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备日志列表
     *
     * @param bo 查询条件
     * @return 设备日志列表
     */
    List<DeviceLogVo> queryList(DeviceLogBo bo);

    /**
     * 新增设备日志
     *
     * @param bo 设备日志
     * @return 是否新增成功
     */
    Boolean insertByBo(DeviceLogBo bo);

    /**
     * 修改设备日志
     *
     * @param bo 设备日志
     * @return 是否修改成功
     */
    Boolean updateByBo(DeviceLogBo bo);

    /**
     * 校验并批量删除设备日志信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
