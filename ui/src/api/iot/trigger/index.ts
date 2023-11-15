import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { TriggerVO, TriggerForm, TriggerQuery } from '@/api/iot/trigger/types';

/**
 * 查询场景联动触发器列表
 * @param query
 * @returns {*}
 */

export const listTrigger = (query?: TriggerQuery): AxiosPromise<TriggerVO[]> => {
  return request({
    url: '/iot/trigger/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询场景联动触发器详细
 * @param sceneTriggerId
 */
export const getTrigger = (sceneTriggerId: string | number): AxiosPromise<TriggerVO> => {
  return request({
    url: '/iot/trigger/' + sceneTriggerId,
    method: 'get'
  });
};

/**
 * 新增场景联动触发器
 * @param data
 */
export const addTrigger = (data: TriggerForm) => {
  return request({
    url: '/iot/trigger',
    method: 'post',
    data: data
  });
};

/**
 * 修改场景联动触发器
 * @param data
 */
export const updateTrigger = (data: TriggerForm) => {
  return request({
    url: '/iot/trigger',
    method: 'put',
    data: data
  });
};

/**
 * 删除场景联动触发器
 * @param sceneTriggerId
 */
export const delTrigger = (sceneTriggerId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/trigger/' + sceneTriggerId,
    method: 'delete'
  });
};
