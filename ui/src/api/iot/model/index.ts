import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ModelVO, ModelForm, ModelQuery } from '@/api/iot/model/types';

/**
 * 查询物模型列表
 * @param query
 * @returns {*}
 */

export const listModel = (query?: ModelQuery): AxiosPromise<ModelVO[]> => {
  return request({
    url: '/iot/model/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询物模型详细
 * @param modelId
 */
export const getModel = (modelId: string | number): AxiosPromise<ModelVO> => {
  return request({
    url: '/iot/model/' + modelId,
    method: 'get'
  });
};

/**
 * 新增物模型
 * @param data
 */
export const addModel = (data: ModelForm) => {
  return request({
    url: '/iot/model',
    method: 'post',
    data: data
  });
};

/**
 * 修改物模型
 * @param data
 */
export const updateModel = (data: ModelForm) => {
  return request({
    url: '/iot/model',
    method: 'put',
    data: data
  });
};

/**
 * 删除物模型
 * @param modelId
 */
export const delModel = (modelId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/model/' + modelId,
    method: 'delete'
  });
};
