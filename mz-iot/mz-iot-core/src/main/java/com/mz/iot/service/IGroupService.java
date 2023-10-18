package com.mz.iot.service;

import com.mz.iot.domain.Group;
import com.mz.iot.domain.vo.GroupVo;
import com.mz.iot.domain.bo.GroupBo;
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
public interface IGroupService {

    /**
     * 查询设备分组
     */
    GroupVo queryById(Long groupId);

    /**
     * 查询设备分组列表
     */
    TableDataInfo<GroupVo> queryPageList(GroupBo bo, PageQuery pageQuery);

    /**
     * 查询设备分组列表
     */
    List<GroupVo> queryList(GroupBo bo);

    /**
     * 新增设备分组
     */
    Boolean insertByBo(GroupBo bo);

    /**
     * 修改设备分组
     */
    Boolean updateByBo(GroupBo bo);

    /**
     * 校验并批量删除设备分组信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
