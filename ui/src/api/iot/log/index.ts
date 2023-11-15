import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LogVO, LogForm, LogQuery } from '@/api/iot/log/types';

/**
 * 查询设备告警日志列表
 * @param query
 * @returns {*}
 */

export const listLog = (query?: LogQuery): AxiosPromise<LogVO[]> => {
  return request({
    url: '/iot/log/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备告警日志详细
 * @param alertLogId
 */
export const getLog = (alertLogId: string | number): AxiosPromise<LogVO> => {
  return request({
    url: '/iot/log/' + alertLogId,
    method: 'get'
  });
};

/**
 * 新增设备告警日志
 * @param data
 */
export const addLog = (data: LogForm) => {
  return request({
    url: '/iot/log',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备告警日志
 * @param data
 */
export const updateLog = (data: LogForm) => {
  return request({
    url: '/iot/log',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备告警日志
 * @param alertLogId
 */
export const delLog = (alertLogId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/log/' + alertLogId,
    method: 'delete'
  });
};
