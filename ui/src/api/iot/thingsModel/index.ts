import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ThingsModelVO, ThingsModelForm, ThingsModelQuery } from '@/api/iot/thingsModel/types';

/**
 * 查询物模型列表
 * @param query
 * @returns {*}
 */

export const listThingsModel = (query?: ThingsModelQuery): AxiosPromise<ThingsModelVO[]> => {
  return request({
    url: '/iot/thingsModel/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询物模型详细
 * @param id
 */
export const getThingsModel = (id: string | number): AxiosPromise<ThingsModelVO> => {
  return request({
    url: '/iot/thingsModel/' + id,
    method: 'get'
  });
};

/**
 * 新增物模型
 * @param data
 */
export const addThingsModel = (data: ThingsModelForm) => {
  return request({
    url: '/iot/thingsModel',
    method: 'post',
    data: data
  });
};

/**
 * 修改物模型
 * @param data
 */
export const updateThingsModel = (data: ThingsModelForm) => {
  return request({
    url: '/iot/thingsModel',
    method: 'put',
    data: data
  });
};

/**
 * 删除物模型
 * @param id
 */
export const delThingsModel = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/thingsModel/' + id,
    method: 'delete'
  });
};
