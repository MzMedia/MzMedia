import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { DeviceUserVO, DeviceUserForm, DeviceUserQuery } from '@/api/iot/deviceUser/types';

/**
 * 查询设备用户列表
 * @param query
 * @returns {*}
 */

export const listDeviceUser = (query?: DeviceUserQuery): AxiosPromise<DeviceUserVO[]> => {
  return request({
    url: '/iot/deviceUser/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备用户详细
 * @param id
 */
export const getDeviceUser = (id: string | number): AxiosPromise<DeviceUserVO> => {
  return request({
    url: '/iot/deviceUser/' + id,
    method: 'get'
  });
};

/**
 * 新增设备用户
 * @param data
 */
export const addDeviceUser = (data: DeviceUserForm) => {
  return request({
    url: '/iot/deviceUser',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备用户
 * @param data
 */
export const updateDeviceUser = (data: DeviceUserForm) => {
  return request({
    url: '/iot/deviceUser',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备用户
 * @param id
 */
export const delDeviceUser = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/deviceUser/' + id,
    method: 'delete'
  });
};
