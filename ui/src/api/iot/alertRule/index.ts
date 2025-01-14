import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { AlertRuleVO, AlertRuleForm, AlertRuleQuery } from '@/api/iot/alertRule/types';

/**
 * 查询设备告警规则列表
 * @param query
 * @returns {*}
 */

export const listAlertRule = (query?: AlertRuleQuery): AxiosPromise<AlertRuleVO[]> => {
  return request({
    url: '/iot/alertRule/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备告警规则详细
 * @param id
 */
export const getAlertRule = (id: string | number): AxiosPromise<AlertRuleVO> => {
  return request({
    url: '/iot/alertRule/' + id,
    method: 'get'
  });
};

/**
 * 新增设备告警规则
 * @param data
 */
export const addAlertRule = (data: AlertRuleForm) => {
  return request({
    url: '/iot/alertRule',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备告警规则
 * @param data
 */
export const updateAlertRule = (data: AlertRuleForm) => {
  return request({
    url: '/iot/alertRule',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备告警规则
 * @param id
 */
export const delAlertRule = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/alertRule/' + id,
    method: 'delete'
  });
};
