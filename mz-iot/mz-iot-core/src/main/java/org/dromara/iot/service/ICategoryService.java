package org.dromara.iot.service;

import org.dromara.iot.domain.vo.CategoryVo;
import org.dromara.iot.domain.bo.CategoryBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 产品分类Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface ICategoryService {

    /**
     * 查询产品分类
     *
     * @param id 主键
     * @return 产品分类
     */
    CategoryVo queryById(Long id);

    /**
     * 分页查询产品分类列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 产品分类分页列表
     */
    TableDataInfo<CategoryVo> queryPageList(CategoryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的产品分类列表
     *
     * @param bo 查询条件
     * @return 产品分类列表
     */
    List<CategoryVo> queryList(CategoryBo bo);

    /**
     * 新增产品分类
     *
     * @param bo 产品分类
     * @return 是否新增成功
     */
    Boolean insertByBo(CategoryBo bo);

    /**
     * 修改产品分类
     *
     * @param bo 产品分类
     * @return 是否修改成功
     */
    Boolean updateByBo(CategoryBo bo);

    /**
     * 校验并批量删除产品分类信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
