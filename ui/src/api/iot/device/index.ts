import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { DeviceVO, DeviceForm, DeviceQuery } from '@/api/iot/device/types';

/**
 * 查询设备信息列表
 * @param query
 * @returns {*}
 */

export const listDevice = (query?: DeviceQuery): AxiosPromise<DeviceVO[]> => {
  return request({
    url: '/iot/device/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备信息详细
 * @param userId
 */
export const getDevice = (userId: string | number): AxiosPromise<DeviceVO> => {
  return request({
    url: '/iot/device/' + userId,
    method: 'get'
  });
};

/**
 * 新增设备信息
 * @param data
 */
export const addDevice = (data: DeviceForm) => {
  return request({
    url: '/iot/device',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备信息
 * @param data
 */
export const updateDevice = (data: DeviceForm) => {
  return request({
    url: '/iot/device',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备信息
 * @param userId
 */
export const delDevice = (userId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/device/' + userId,
    method: 'delete'
  });
};
