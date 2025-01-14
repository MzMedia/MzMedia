package org.dromara.iot.service;

import org.dromara.iot.domain.vo.ProductVo;
import org.dromara.iot.domain.bo.ProductBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 产品Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IProductService {

    /**
     * 查询产品
     *
     * @param id 主键
     * @return 产品
     */
    ProductVo queryById(Long id);

    /**
     * 分页查询产品列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 产品分页列表
     */
    TableDataInfo<ProductVo> queryPageList(ProductBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的产品列表
     *
     * @param bo 查询条件
     * @return 产品列表
     */
    List<ProductVo> queryList(ProductBo bo);

    /**
     * 新增产品
     *
     * @param bo 产品
     * @return 是否新增成功
     */
    Boolean insertByBo(ProductBo bo);

    /**
     * 修改产品
     *
     * @param bo 产品
     * @return 是否修改成功
     */
    Boolean updateByBo(ProductBo bo);

    /**
     * 校验并批量删除产品信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
