import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { DeviceChannelVO, DeviceChannelForm, DeviceChannelQuery } from '@/api/iot/deviceChannel/types';

/**
 * 查询监控设备通道信息列表
 * @param query
 * @returns {*}
 */

export const listDeviceChannel = (query?: DeviceChannelQuery): AxiosPromise<DeviceChannelVO[]> => {
  return request({
    url: '/iot/deviceChannel/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询监控设备通道信息详细
 * @param id
 */
export const getDeviceChannel = (id: string | number): AxiosPromise<DeviceChannelVO> => {
  return request({
    url: '/iot/deviceChannel/' + id,
    method: 'get'
  });
};

/**
 * 新增监控设备通道信息
 * @param data
 */
export const addDeviceChannel = (data: DeviceChannelForm) => {
  return request({
    url: '/iot/deviceChannel',
    method: 'post',
    data: data
  });
};

/**
 * 修改监控设备通道信息
 * @param data
 */
export const updateDeviceChannel = (data: DeviceChannelForm) => {
  return request({
    url: '/iot/deviceChannel',
    method: 'put',
    data: data
  });
};

/**
 * 删除监控设备通道信息
 * @param id
 */
export const delDeviceChannel = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/deviceChannel/' + id,
    method: 'delete'
  });
};
