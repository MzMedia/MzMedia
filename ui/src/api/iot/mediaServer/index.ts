import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { MediaServerVO, MediaServerForm, MediaServerQuery } from '@/api/iot/mediaServer/types';

/**
 * 查询流媒体服务器配置列表
 * @param query
 * @returns {*}
 */

export const listMediaServer = (query?: MediaServerQuery): AxiosPromise<MediaServerVO[]> => {
  return request({
    url: '/iot/mediaServer/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询流媒体服务器配置详细
 * @param id
 */
export const getMediaServer = (id: string | number): AxiosPromise<MediaServerVO> => {
  return request({
    url: '/iot/mediaServer/' + id,
    method: 'get'
  });
};

/**
 * 新增流媒体服务器配置
 * @param data
 */
export const addMediaServer = (data: MediaServerForm) => {
  return request({
    url: '/iot/mediaServer',
    method: 'post',
    data: data
  });
};

/**
 * 修改流媒体服务器配置
 * @param data
 */
export const updateMediaServer = (data: MediaServerForm) => {
  return request({
    url: '/iot/mediaServer',
    method: 'put',
    data: data
  });
};

/**
 * 删除流媒体服务器配置
 * @param id
 */
export const delMediaServer = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/mediaServer/' + id,
    method: 'delete'
  });
};
