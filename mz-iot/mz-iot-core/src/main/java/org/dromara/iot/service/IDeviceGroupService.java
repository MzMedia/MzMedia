package org.dromara.iot.service;

import org.dromara.iot.domain.vo.DeviceGroupVo;
import org.dromara.iot.domain.bo.DeviceGroupBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备分组Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IDeviceGroupService {

    /**
     * 查询设备分组
     *
     * @param groupId 主键
     * @return 设备分组
     */
    DeviceGroupVo queryById(Long groupId);

    /**
     * 分页查询设备分组列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备分组分页列表
     */
    TableDataInfo<DeviceGroupVo> queryPageList(DeviceGroupBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备分组列表
     *
     * @param bo 查询条件
     * @return 设备分组列表
     */
    List<DeviceGroupVo> queryList(DeviceGroupBo bo);

    /**
     * 新增设备分组
     *
     * @param bo 设备分组
     * @return 是否新增成功
     */
    Boolean insertByBo(DeviceGroupBo bo);

    /**
     * 修改设备分组
     *
     * @param bo 设备分组
     * @return 是否修改成功
     */
    Boolean updateByBo(DeviceGroupBo bo);

    /**
     * 校验并批量删除设备分组信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
