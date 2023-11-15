import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ConfigVO, ConfigForm, ConfigQuery } from '@/api/iot/config/types';

/**
 * 查询sip系统配置列表
 * @param query
 * @returns {*}
 */

export const listConfig = (query?: ConfigQuery): AxiosPromise<ConfigVO[]> => {
  return request({
    url: '/iot/config/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询sip系统配置详细
 * @param id
 */
export const getConfig = (id: string | number): AxiosPromise<ConfigVO> => {
  return request({
    url: '/iot/config/' + id,
    method: 'get'
  });
};

/**
 * 新增sip系统配置
 * @param data
 */
export const addConfig = (data: ConfigForm) => {
  return request({
    url: '/iot/config',
    method: 'post',
    data: data
  });
};

/**
 * 修改sip系统配置
 * @param data
 */
export const updateConfig = (data: ConfigForm) => {
  return request({
    url: '/iot/config',
    method: 'put',
    data: data
  });
};

/**
 * 删除sip系统配置
 * @param id
 */
export const delConfig = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/config/' + id,
    method: 'delete'
  });
};
