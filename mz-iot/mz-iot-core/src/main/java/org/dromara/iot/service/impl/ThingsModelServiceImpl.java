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
import org.dromara.iot.domain.bo.ThingsModelBo;
import org.dromara.iot.domain.vo.ThingsModelVo;
import org.dromara.iot.domain.ThingsModel;
import org.dromara.iot.mapper.ThingsModelMapper;
import org.dromara.iot.service.IThingsModelService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 物模型Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class ThingsModelServiceImpl implements IThingsModelService {

    private final ThingsModelMapper baseMapper;

    /**
     * 查询物模型
     *
     * @param id 主键
     * @return 物模型
     */
    @Override
    public ThingsModelVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询物模型列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 物模型分页列表
     */
    @Override
    public TableDataInfo<ThingsModelVo> queryPageList(ThingsModelBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ThingsModel> lqw = buildQueryWrapper(bo);
        Page<ThingsModelVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的物模型列表
     *
     * @param bo 查询条件
     * @return 物模型列表
     */
    @Override
    public List<ThingsModelVo> queryList(ThingsModelBo bo) {
        LambdaQueryWrapper<ThingsModel> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ThingsModel> buildQueryWrapper(ThingsModelBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ThingsModel> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getModelName()), ThingsModel::getModelName, bo.getModelName());
        lqw.eq(bo.getProductId() != null, ThingsModel::getProductId, bo.getProductId());
        lqw.eq(StringUtils.isNotBlank(bo.getIdentifier()), ThingsModel::getIdentifier, bo.getIdentifier());
        lqw.eq(bo.getType() != null, ThingsModel::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getDatatype()), ThingsModel::getDatatype, bo.getDatatype());
        lqw.eq(StringUtils.isNotBlank(bo.getSpecs()), ThingsModel::getSpecs, bo.getSpecs());
        lqw.eq(bo.getIsChart() != null, ThingsModel::getIsChart, bo.getIsChart());
        lqw.eq(bo.getIsMonitor() != null, ThingsModel::getIsMonitor, bo.getIsMonitor());
        lqw.eq(bo.getIsHistory() != null, ThingsModel::getIsHistory, bo.getIsHistory());
        lqw.eq(bo.getIsReadonly() != null, ThingsModel::getIsReadonly, bo.getIsReadonly());
        lqw.eq(bo.getIsSharePerm() != null, ThingsModel::getIsSharePerm, bo.getIsSharePerm());
        lqw.eq(bo.getModelOrder() != null, ThingsModel::getModelOrder, bo.getModelOrder());
        return lqw;
    }

    /**
     * 新增物模型
     *
     * @param bo 物模型
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ThingsModelBo bo) {
        ThingsModel add = MapstructUtils.convert(bo, ThingsModel.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改物模型
     *
     * @param bo 物模型
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ThingsModelBo bo) {
        ThingsModel update = MapstructUtils.convert(bo, ThingsModel.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ThingsModel entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除物模型信息
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
