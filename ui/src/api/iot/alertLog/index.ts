import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { AlertLogVO, AlertLogForm, AlertLogQuery } from '@/api/iot/alertLog/types';

/**
 * 查询设备告警日志列表
 * @param query
 * @returns {*}
 */

export const listAlertLog = (query?: AlertLogQuery): AxiosPromise<AlertLogVO[]> => {
  return request({
    url: '/iot/alertLog/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备告警日志详细
 * @param id
 */
export const getAlertLog = (id: string | number): AxiosPromise<AlertLogVO> => {
  return request({
    url: '/iot/alertLog/' + id,
    method: 'get'
  });
};

/**
 * 新增设备告警日志
 * @param data
 */
export const addAlertLog = (data: AlertLogForm) => {
  return request({
    url: '/iot/alertLog',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备告警日志
 * @param data
 */
export const updateAlertLog = (data: AlertLogForm) => {
  return request({
    url: '/iot/alertLog',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备告警日志
 * @param id
 */
export const delAlertLog = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/alertLog/' + id,
    method: 'delete'
  });
};
