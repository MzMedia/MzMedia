package org.dromara.iot.service;

import org.dromara.iot.domain.vo.DeviceUserVo;
import org.dromara.iot.domain.bo.DeviceUserBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 设备用户Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IDeviceUserService {

    /**
     * 查询设备用户
     *
     * @param id 主键
     * @return 设备用户
     */
    DeviceUserVo queryById(Long id);

    /**
     * 分页查询设备用户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备用户分页列表
     */
    TableDataInfo<DeviceUserVo> queryPageList(DeviceUserBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备用户列表
     *
     * @param bo 查询条件
     * @return 设备用户列表
     */
    List<DeviceUserVo> queryList(DeviceUserBo bo);

    /**
     * 新增设备用户
     *
     * @param bo 设备用户
     * @return 是否新增成功
     */
    Boolean insertByBo(DeviceUserBo bo);

    /**
     * 修改设备用户
     *
     * @param bo 设备用户
     * @return 是否修改成功
     */
    Boolean updateByBo(DeviceUserBo bo);

    /**
     * 校验并批量删除设备用户信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
