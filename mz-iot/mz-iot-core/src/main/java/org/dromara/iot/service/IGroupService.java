package org.dromara.iot.service;

import org.dromara.iot.domain.vo.GroupVo;
import org.dromara.iot.domain.bo.GroupBo;
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
public interface IGroupService {

    /**
     * 查询设备分组
     *
     * @param id 主键
     * @return 设备分组
     */
    GroupVo queryById(Long id);

    /**
     * 分页查询设备分组列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备分组分页列表
     */
    TableDataInfo<GroupVo> queryPageList(GroupBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的设备分组列表
     *
     * @param bo 查询条件
     * @return 设备分组列表
     */
    List<GroupVo> queryList(GroupBo bo);

    /**
     * 新增设备分组
     *
     * @param bo 设备分组
     * @return 是否新增成功
     */
    Boolean insertByBo(GroupBo bo);

    /**
     * 修改设备分组
     *
     * @param bo 设备分组
     * @return 是否修改成功
     */
    Boolean updateByBo(GroupBo bo);

    /**
     * 校验并批量删除设备分组信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
