package com.mz.system.mapper;

import com.mz.system.domain.SysUserRole;
import com.mz.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 用户与角色关联表 数据层
 *
 * @author Lion Li
 */
public interface SysUserRoleMapper extends BaseMapperPlus<SysUserRole, SysUserRole> {

    List<Long> selectUserIdsByRoleId(Long roleId);

}
