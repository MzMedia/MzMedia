package org.dromara.iot.service;

import org.dromara.iot.domain.vo.DeviceVo;
import org.dromara.iot.domain.bo.DeviceBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备信息Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IDeviceService {

    /**
     * 查询设备信息
     *
     * @param id 主键
     * @return 设备信息
     */
    DeviceVo queryById(Long id);

    /**
     * 分页查询设备信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备信息分页列表
     */
    TableDataInfo<DeviceVo> queryPageList(DeviceBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备信息列表
     *
     * @param bo 查询条件
     * @return 设备信息列表
     */
    List<DeviceVo> queryList(DeviceBo bo);

    /**
     * 新增设备信息
     *
     * @param bo 设备信息
     * @return 是否新增成功
     */
    Boolean insertByBo(DeviceBo bo);

    /**
     * 修改设备信息
     *
     * @param bo 设备信息
     * @return 是否修改成功
     */
    Boolean updateByBo(DeviceBo bo);

    /**
     * 校验并批量删除设备信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
