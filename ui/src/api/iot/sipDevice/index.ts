import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { SipDeviceVO, SipDeviceForm, SipDeviceQuery } from '@/api/iot/sipDevice/types';

/**
 * 查询监控设备列表
 * @param query
 * @returns {*}
 */

export const listSipDevice = (query?: SipDeviceQuery): AxiosPromise<SipDeviceVO[]> => {
  return request({
    url: '/iot/sipDevice/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询监控设备详细
 * @param id
 */
export const getSipDevice = (id: string | number): AxiosPromise<SipDeviceVO> => {
  return request({
    url: '/iot/sipDevice/' + id,
    method: 'get'
  });
};

/**
 * 新增监控设备
 * @param data
 */
export const addSipDevice = (data: SipDeviceForm) => {
  return request({
    url: '/iot/sipDevice',
    method: 'post',
    data: data
  });
};

/**
 * 修改监控设备
 * @param data
 */
export const updateSipDevice = (data: SipDeviceForm) => {
  return request({
    url: '/iot/sipDevice',
    method: 'put',
    data: data
  });
};

/**
 * 删除监控设备
 * @param id
 */
export const delSipDevice = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/sipDevice/' + id,
    method: 'delete'
  });
};
