package com.mz.iot.service;

import com.mz.iot.domain.ThingsModelTemplate;
import com.mz.iot.domain.vo.ThingsModelTemplateVo;
import com.mz.iot.domain.bo.ThingsModelTemplateBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 物模型模板Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IThingsModelTemplateService {

    /**
     * 查询物模型模板
     */
    ThingsModelTemplateVo queryById(Long templateId);

    /**
     * 查询物模型模板列表
     */
    TableDataInfo<ThingsModelTemplateVo> queryPageList(ThingsModelTemplateBo bo, PageQuery pageQuery);

    /**
     * 查询物模型模板列表
     */
    List<ThingsModelTemplateVo> queryList(ThingsModelTemplateBo bo);

    /**
     * 新增物模型模板
     */
    Boolean insertByBo(ThingsModelTemplateBo bo);

    /**
     * 修改物模型模板
     */
    Boolean updateByBo(ThingsModelTemplateBo bo);

    /**
     * 校验并批量删除物模型模板信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
