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
import com.mz.iot.domain.bo.ProductBo;
import com.mz.iot.domain.vo.ProductVo;
import com.mz.iot.domain.Product;
import com.mz.iot.mapper.ProductMapper;
import com.mz.iot.service.IProductService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 产品Service业务层处理
 *
 * @author MzMedia
 * @date 2023-10-18
 */
@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements IProductService {

    private final ProductMapper baseMapper;

    /**
     * 查询产品
     */
    @Override
    public ProductVo queryById(Long productId){
        return baseMapper.selectVoById(productId);
    }

    /**
     * 查询产品列表
     */
    @Override
    public TableDataInfo<ProductVo> queryPageList(ProductBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Product> lqw = buildQueryWrapper(bo);
        Page<ProductVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询产品列表
     */
    @Override
    public List<ProductVo> queryList(ProductBo bo) {
        LambdaQueryWrapper<Product> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Product> buildQueryWrapper(ProductBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Product> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getCategoryId() != null, Product::getCategoryId, bo.getCategoryId());
        lqw.like(StringUtils.isNotBlank(bo.getProductName()), Product::getProductName, bo.getProductName());
        lqw.eq(StringUtils.isNotBlank(bo.getProtocolCode()), Product::getProtocolCode, bo.getProtocolCode());
        lqw.eq(bo.getIsSys() != null, Product::getIsSys, bo.getIsSys());
        lqw.eq(bo.getIsAuthorize() != null, Product::getIsAuthorize, bo.getIsAuthorize());
        lqw.eq(StringUtils.isNotBlank(bo.getMqttAccount()), Product::getMqttAccount, bo.getMqttAccount());
        lqw.eq(StringUtils.isNotBlank(bo.getMqttPassword()), Product::getMqttPassword, bo.getMqttPassword());
        lqw.eq(StringUtils.isNotBlank(bo.getMqttSecret()), Product::getMqttSecret, bo.getMqttSecret());
        lqw.eq(bo.getStatus() != null, Product::getStatus, bo.getStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getThingsModelsJson()), Product::getThingsModelsJson, bo.getThingsModelsJson());
        lqw.eq(bo.getDeviceType() != null, Product::getDeviceType, bo.getDeviceType());
        lqw.eq(bo.getNetworkMethod() != null, Product::getNetworkMethod, bo.getNetworkMethod());
        lqw.eq(bo.getVertificateMethod() != null, Product::getVertificateMethod, bo.getVertificateMethod());
        lqw.eq(StringUtils.isNotBlank(bo.getImgUrl()), Product::getImgUrl, bo.getImgUrl());
        return lqw;
    }

    /**
     * 新增产品
     */
    @Override
    public Boolean insertByBo(ProductBo bo) {
        Product add = MapstructUtils.convert(bo, Product.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setProductId(add.getProductId());
        }
        return flag;
    }

    /**
     * 修改产品
     */
    @Override
    public Boolean updateByBo(ProductBo bo) {
        Product update = MapstructUtils.convert(bo, Product.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Product entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除产品
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }
}
