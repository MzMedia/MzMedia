import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CategoryVO, CategoryForm, CategoryQuery } from '@/api/iot/category/types';

/**
 * 查询产品分类列表
 * @param query
 * @returns {*}
 */

export const listCategory = (query?: CategoryQuery): AxiosPromise<CategoryVO[]> => {
  return request({
    url: '/iot/category/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询产品分类详细
 * @param categoryId
 */
export const getCategory = (categoryId: string | number): AxiosPromise<CategoryVO> => {
  return request({
    url: '/iot/category/' + categoryId,
    method: 'get'
  });
};

/**
 * 新增产品分类
 * @param data
 */
export const addCategory = (data: CategoryForm) => {
  return request({
    url: '/iot/category',
    method: 'post',
    data: data
  });
};

/**
 * 修改产品分类
 * @param data
 */
export const updateCategory = (data: CategoryForm) => {
  return request({
    url: '/iot/category',
    method: 'put',
    data: data
  });
};

/**
 * 删除产品分类
 * @param categoryId
 */
export const delCategory = (categoryId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/category/' + categoryId,
    method: 'delete'
  });
};
