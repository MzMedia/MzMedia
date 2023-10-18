package com.mz.iot.service;

import com.mz.iot.domain.Product;
import com.mz.iot.domain.vo.ProductVo;
import com.mz.iot.domain.bo.ProductBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 产品Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IProductService {

    /**
     * 查询产品
     */
    ProductVo queryById(Long productId);

    /**
     * 查询产品列表
     */
    TableDataInfo<ProductVo> queryPageList(ProductBo bo, PageQuery pageQuery);

    /**
     * 查询产品列表
     */
    List<ProductVo> queryList(ProductBo bo);

    /**
     * 新增产品
     */
    Boolean insertByBo(ProductBo bo);

    /**
     * 修改产品
     */
    Boolean updateByBo(ProductBo bo);

    /**
     * 校验并批量删除产品信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
