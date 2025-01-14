import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { DeviceGroupVO, DeviceGroupForm, DeviceGroupQuery } from '@/api/iot/deviceGroup/types';

/**
 * 查询设备分组列表
 * @param query
 * @returns {*}
 */

export const listDeviceGroup = (query?: DeviceGroupQuery): AxiosPromise<DeviceGroupVO[]> => {
  return request({
    url: '/iot/deviceGroup/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备分组详细
 * @param groupId
 */
export const getDeviceGroup = (groupId: string | number): AxiosPromise<DeviceGroupVO> => {
  return request({
    url: '/iot/deviceGroup/' + groupId,
    method: 'get'
  });
};

/**
 * 新增设备分组
 * @param data
 */
export const addDeviceGroup = (data: DeviceGroupForm) => {
  return request({
    url: '/iot/deviceGroup',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备分组
 * @param data
 */
export const updateDeviceGroup = (data: DeviceGroupForm) => {
  return request({
    url: '/iot/deviceGroup',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备分组
 * @param groupId
 */
export const delDeviceGroup = (groupId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/deviceGroup/' + groupId,
    method: 'delete'
  });
};
