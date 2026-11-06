import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { SceneTriggerVO, SceneTriggerForm, SceneTriggerQuery } from '@/api/iot/sceneTrigger/types';

/**
 * 查询场景联动触发器列表
 * @param query
 * @returns {*}
 */

export const listSceneTrigger = (query?: SceneTriggerQuery): AxiosPromise<SceneTriggerVO[]> => {
  return request({
    url: '/iot/sceneTrigger/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询场景联动触发器详细
 * @param id
 */
export const getSceneTrigger = (id: string | number): AxiosPromise<SceneTriggerVO> => {
  return request({
    url: '/iot/sceneTrigger/' + id,
    method: 'get'
  });
};

/**
 * 新增场景联动触发器
 * @param data
 */
export const addSceneTrigger = (data: SceneTriggerForm) => {
  return request({
    url: '/iot/sceneTrigger',
    method: 'post',
    data: data
  });
};

/**
 * 修改场景联动触发器
 * @param data
 */
export const updateSceneTrigger = (data: SceneTriggerForm) => {
  return request({
    url: '/iot/sceneTrigger',
    method: 'put',
    data: data
  });
};

/**
 * 删除场景联动触发器
 * @param id
 */
export const delSceneTrigger = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/sceneTrigger/' + id,
    method: 'delete'
  });
};
