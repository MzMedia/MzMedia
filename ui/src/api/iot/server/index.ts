import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ServerVO, ServerForm, ServerQuery } from '@/api/iot/server/types';

/**
 * 查询流媒体服务器配置列表
 * @param query
 * @returns {*}
 */

export const listServer = (query?: ServerQuery): AxiosPromise<ServerVO[]> => {
  return request({
    url: '/iot/server/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询流媒体服务器配置详细
 * @param id
 */
export const getServer = (id: string | number): AxiosPromise<ServerVO> => {
  return request({
    url: '/iot/server/' + id,
    method: 'get'
  });
};

/**
 * 新增流媒体服务器配置
 * @param data
 */
export const addServer = (data: ServerForm) => {
  return request({
    url: '/iot/server',
    method: 'post',
    data: data
  });
};

/**
 * 修改流媒体服务器配置
 * @param data
 */
export const updateServer = (data: ServerForm) => {
  return request({
    url: '/iot/server',
    method: 'put',
    data: data
  });
};

/**
 * 删除流媒体服务器配置
 * @param id
 */
export const delServer = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/server/' + id,
    method: 'delete'
  });
};
