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
import org.dromara.iot.domain.bo.ThingsModelTemplateBo;
import org.dromara.iot.domain.vo.ThingsModelTemplateVo;
import org.dromara.iot.domain.ThingsModelTemplate;
import org.dromara.iot.mapper.ThingsModelTemplateMapper;
import org.dromara.iot.service.IThingsModelTemplateService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 物模型模板Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class ThingsModelTemplateServiceImpl implements IThingsModelTemplateService {

    private final ThingsModelTemplateMapper baseMapper;

    /**
     * 查询物模型模板
     *
     * @param id 主键
     * @return 物模型模板
     */
    @Override
    public ThingsModelTemplateVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询物模型模板列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 物模型模板分页列表
     */
    @Override
    public TableDataInfo<ThingsModelTemplateVo> queryPageList(ThingsModelTemplateBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ThingsModelTemplate> lqw = buildQueryWrapper(bo);
        Page<ThingsModelTemplateVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的物模型模板列表
     *
     * @param bo 查询条件
     * @return 物模型模板列表
     */
    @Override
    public List<ThingsModelTemplateVo> queryList(ThingsModelTemplateBo bo) {
        LambdaQueryWrapper<ThingsModelTemplate> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ThingsModelTemplate> buildQueryWrapper(ThingsModelTemplateBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ThingsModelTemplate> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTemplateName()), ThingsModelTemplate::getTemplateName, bo.getTemplateName());
        lqw.eq(StringUtils.isNotBlank(bo.getIdentifier()), ThingsModelTemplate::getIdentifier, bo.getIdentifier());
        lqw.eq(bo.getType() != null, ThingsModelTemplate::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getDatatype()), ThingsModelTemplate::getDatatype, bo.getDatatype());
        lqw.eq(StringUtils.isNotBlank(bo.getSpecs()), ThingsModelTemplate::getSpecs, bo.getSpecs());
        lqw.eq(bo.getIsSys() != null, ThingsModelTemplate::getIsSys, bo.getIsSys());
        lqw.eq(bo.getIsChart() != null, ThingsModelTemplate::getIsChart, bo.getIsChart());
        lqw.eq(bo.getIsMonitor() != null, ThingsModelTemplate::getIsMonitor, bo.getIsMonitor());
        lqw.eq(bo.getIsHistory() != null, ThingsModelTemplate::getIsHistory, bo.getIsHistory());
        lqw.eq(bo.getIsReadonly() != null, ThingsModelTemplate::getIsReadonly, bo.getIsReadonly());
        lqw.eq(bo.getIsSharePerm() != null, ThingsModelTemplate::getIsSharePerm, bo.getIsSharePerm());
        lqw.eq(bo.getModelOrder() != null, ThingsModelTemplate::getModelOrder, bo.getModelOrder());
        return lqw;
    }

    /**
     * 新增物模型模板
     *
     * @param bo 物模型模板
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ThingsModelTemplateBo bo) {
        ThingsModelTemplate add = MapstructUtils.convert(bo, ThingsModelTemplate.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改物模型模板
     *
     * @param bo 物模型模板
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ThingsModelTemplateBo bo) {
        ThingsModelTemplate update = MapstructUtils.convert(bo, ThingsModelTemplate.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ThingsModelTemplate entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除物模型模板信息
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
