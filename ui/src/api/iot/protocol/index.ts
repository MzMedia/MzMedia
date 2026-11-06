import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProtocolVO, ProtocolForm, ProtocolQuery } from '@/api/iot/protocol/types';

/**
 * 查询协议列表
 * @param query
 * @returns {*}
 */

export const listProtocol = (query?: ProtocolQuery): AxiosPromise<ProtocolVO[]> => {
  return request({
    url: '/iot/protocol/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询协议详细
 * @param id
 */
export const getProtocol = (id: string | number): AxiosPromise<ProtocolVO> => {
  return request({
    url: '/iot/protocol/' + id,
    method: 'get'
  });
};

/**
 * 新增协议
 * @param data
 */
export const addProtocol = (data: ProtocolForm) => {
  return request({
    url: '/iot/protocol',
    method: 'post',
    data: data
  });
};

/**
 * 修改协议
 * @param data
 */
export const updateProtocol = (data: ProtocolForm) => {
  return request({
    url: '/iot/protocol',
    method: 'put',
    data: data
  });
};

/**
 * 删除协议
 * @param id
 */
export const delProtocol = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/protocol/' + id,
    method: 'delete'
  });
};
