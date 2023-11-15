import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { RuleVO, RuleForm, RuleQuery } from '@/api/iot/rule/types';

/**
 * 查询设备告警规则列表
 * @param query
 * @returns {*}
 */

export const listRule = (query?: RuleQuery): AxiosPromise<RuleVO[]> => {
  return request({
    url: '/iot/rule/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备告警规则详细
 * @param alertId
 */
export const getRule = (alertId: string | number): AxiosPromise<RuleVO> => {
  return request({
    url: '/iot/rule/' + alertId,
    method: 'get'
  });
};

/**
 * 新增设备告警规则
 * @param data
 */
export const addRule = (data: RuleForm) => {
  return request({
    url: '/iot/rule',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备告警规则
 * @param data
 */
export const updateRule = (data: RuleForm) => {
  return request({
    url: '/iot/rule',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备告警规则
 * @param alertId
 */
export const delRule = (alertId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/rule/' + alertId,
    method: 'delete'
  });
};
