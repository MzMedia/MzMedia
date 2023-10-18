package com.mz.iot.service;

import com.mz.iot.domain.Category;
import com.mz.iot.domain.vo.CategoryVo;
import com.mz.iot.domain.bo.CategoryBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 产品分类Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface ICategoryService {

    /**
     * 查询产品分类
     */
    CategoryVo queryById(Long categoryId);

    /**
     * 查询产品分类列表
     */
    TableDataInfo<CategoryVo> queryPageList(CategoryBo bo, PageQuery pageQuery);

    /**
     * 查询产品分类列表
     */
    List<CategoryVo> queryList(CategoryBo bo);

    /**
     * 新增产品分类
     */
    Boolean insertByBo(CategoryBo bo);

    /**
     * 修改产品分类
     */
    Boolean updateByBo(CategoryBo bo);

    /**
     * 校验并批量删除产品分类信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
