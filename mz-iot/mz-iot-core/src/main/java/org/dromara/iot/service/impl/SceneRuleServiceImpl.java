package org.dromara.iot.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.iot.domain.bo.SceneRuleBo;
import org.dromara.iot.domain.vo.SceneRuleVo;
import org.dromara.iot.domain.SceneRule;
import org.dromara.iot.mapper.SceneRuleMapper;
import org.dromara.iot.service.ISceneRuleService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 场景联动Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class SceneRuleServiceImpl implements ISceneRuleService {

    private final SceneRuleMapper baseMapper;

    /**
     * 查询场景联动
     *
     * @param id 主键
     * @return 场景联动
     */
    @Override
    public SceneRuleVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询场景联动列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 场景联动分页列表
     */
    @Override
    public TableDataInfo<SceneRuleVo> queryPageList(SceneRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<SceneRule> lqw = buildQueryWrapper(bo);
        Page<SceneRuleVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的场景联动列表
     *
     * @param bo 查询条件
     * @return 场景联动列表
     */
    @Override
    public List<SceneRuleVo> queryList(SceneRuleBo bo) {
        LambdaQueryWrapper<SceneRule> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<SceneRule> buildQueryWrapper(SceneRuleBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<SceneRule> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getSceneName()), SceneRule::getSceneName, bo.getSceneName());
        lqw.eq(StringUtils.isNotBlank(bo.getActions()), SceneRule::getActions, bo.getActions());
        lqw.eq(bo.getStatus() != null, SceneRule::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增场景联动
     *
     * @param bo 场景联动
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(SceneRuleBo bo) {
        SceneRule add = MapstructUtils.convert(bo, SceneRule.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改场景联动
     *
     * @param bo 场景联动
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(SceneRuleBo bo) {
        SceneRule update = MapstructUtils.convert(bo, SceneRule.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SceneRule entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除场景联动信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
