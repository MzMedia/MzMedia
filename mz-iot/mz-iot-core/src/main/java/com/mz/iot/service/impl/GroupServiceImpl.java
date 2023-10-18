package com.mz.iot.service.impl;

import com.mz.common.core.utils.MapstructUtils;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.mz.common.core.utils.StringUtils;
import com.mz.iot.domain.bo.GroupBo;
import com.mz.iot.domain.vo.GroupVo;
import com.mz.iot.domain.Group;
import com.mz.iot.mapper.GroupMapper;
import com.mz.iot.service.IGroupService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备分组Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class GroupServiceImpl implements IGroupService {

    private final GroupMapper baseMapper;

    /**
     * 查询设备分组
     */
    @Override
    public GroupVo queryById(Long groupId){
        return baseMapper.selectVoById(groupId);
    }

    /**
     * 查询设备分组列表
     */
    @Override
    public TableDataInfo<GroupVo> queryPageList(GroupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Group> lqw = buildQueryWrapper(bo);
        Page<GroupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询设备分组列表
     */
    @Override
    public List<GroupVo> queryList(GroupBo bo) {
        LambdaQueryWrapper<Group> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Group> buildQueryWrapper(GroupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Group> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, Group::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getGroupName()), Group::getGroupName, bo.getGroupName());
        lqw.eq(bo.getGroupOrder() != null, Group::getGroupOrder, bo.getGroupOrder());
        return lqw;
    }

    /**
     * 新增设备分组
     */
    @Override
    public Boolean insertByBo(GroupBo bo) {
        Group add = MapstructUtils.convert(bo, Group.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setGroupId(add.getGroupId());
        }
        return flag;
    }

    /**
     * 修改设备分组
     */
    @Override
    public Boolean updateByBo(GroupBo bo) {
        Group update = MapstructUtils.convert(bo, Group.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Group entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除设备分组
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
