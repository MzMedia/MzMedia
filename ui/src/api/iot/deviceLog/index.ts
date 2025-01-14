import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { DeviceLogVO, DeviceLogForm, DeviceLogQuery } from '@/api/iot/deviceLog/types';

/**
 * 查询设备日志列表
 * @param query
 * @returns {*}
 */

export const listDeviceLog = (query?: DeviceLogQuery): AxiosPromise<DeviceLogVO[]> => {
  return request({
    url: '/iot/deviceLog/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备日志详细
 * @param id
 */
export const getDeviceLog = (id: string | number): AxiosPromise<DeviceLogVO> => {
  return request({
    url: '/iot/deviceLog/' + id,
    method: 'get'
  });
};

/**
 * 新增设备日志
 * @param data
 */
export const addDeviceLog = (data: DeviceLogForm) => {
  return request({
    url: '/iot/deviceLog',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备日志
 * @param data
 */
export const updateDeviceLog = (data: DeviceLogForm) => {
  return request({
    url: '/iot/deviceLog',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备日志
 * @param id
 */
export const delDeviceLog = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/deviceLog/' + id,
    method: 'delete'
  });
};
