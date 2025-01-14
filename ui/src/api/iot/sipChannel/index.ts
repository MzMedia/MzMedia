import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { SipChannelVO, SipChannelForm, SipChannelQuery } from '@/api/iot/sipChannel/types';

/**
 * 查询监控设备通道信息列表
 * @param query
 * @returns {*}
 */

export const listSipChannel = (query?: SipChannelQuery): AxiosPromise<SipChannelVO[]> => {
  return request({
    url: '/iot/sipChannel/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询监控设备通道信息详细
 * @param id
 */
export const getSipChannel = (id: string | number): AxiosPromise<SipChannelVO> => {
  return request({
    url: '/iot/sipChannel/' + id,
    method: 'get'
  });
};

/**
 * 新增监控设备通道信息
 * @param data
 */
export const addSipChannel = (data: SipChannelForm) => {
  return request({
    url: '/iot/sipChannel',
    method: 'post',
    data: data
  });
};

/**
 * 修改监控设备通道信息
 * @param data
 */
export const updateSipChannel = (data: SipChannelForm) => {
  return request({
    url: '/iot/sipChannel',
    method: 'put',
    data: data
  });
};

/**
 * 删除监控设备通道信息
 * @param id
 */
export const delSipChannel = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/sipChannel/' + id,
    method: 'delete'
  });
};
