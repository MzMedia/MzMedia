package com.mz.iot.service;

import com.mz.iot.domain.ThingsModel;
import com.mz.iot.domain.vo.ThingsModelVo;
import com.mz.iot.domain.bo.ThingsModelBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 物模型Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IThingsModelService {

    /**
     * 查询物模型
     */
    ThingsModelVo queryById(Long modelId);

    /**
     * 查询物模型列表
     */
    TableDataInfo<ThingsModelVo> queryPageList(ThingsModelBo bo, PageQuery pageQuery);

    /**
     * 查询物模型列表
     */
    List<ThingsModelVo> queryList(ThingsModelBo bo);

    /**
     * 新增物模型
     */
    Boolean insertByBo(ThingsModelBo bo);

    /**
     * 修改物模型
     */
    Boolean updateByBo(ThingsModelBo bo);

    /**
     * 校验并批量删除物模型信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
