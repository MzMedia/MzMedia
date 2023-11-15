import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProductVO, ProductForm, ProductQuery } from '@/api/iot/product/types';

/**
 * 查询产品列表
 * @param query
 * @returns {*}
 */

export const listProduct = (query?: ProductQuery): AxiosPromise<ProductVO[]> => {
  return request({
    url: '/iot/product/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询产品详细
 * @param productId
 */
export const getProduct = (productId: string | number): AxiosPromise<ProductVO> => {
  return request({
    url: '/iot/product/' + productId,
    method: 'get'
  });
};

/**
 * 新增产品
 * @param data
 */
export const addProduct = (data: ProductForm) => {
  return request({
    url: '/iot/product',
    method: 'post',
    data: data
  });
};

/**
 * 修改产品
 * @param data
 */
export const updateProduct = (data: ProductForm) => {
  return request({
    url: '/iot/product',
    method: 'put',
    data: data
  });
};

/**
 * 删除产品
 * @param productId
 */
export const delProduct = (productId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/product/' + productId,
    method: 'delete'
  });
};
