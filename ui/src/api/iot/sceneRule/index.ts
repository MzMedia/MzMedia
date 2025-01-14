import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { SceneRuleVO, SceneRuleForm, SceneRuleQuery } from '@/api/iot/sceneRule/types';

/**
 * 查询场景联动列表
 * @param query
 * @returns {*}
 */

export const listSceneRule = (query?: SceneRuleQuery): AxiosPromise<SceneRuleVO[]> => {
  return request({
    url: '/iot/sceneRule/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询场景联动详细
 * @param id
 */
export const getSceneRule = (id: string | number): AxiosPromise<SceneRuleVO> => {
  return request({
    url: '/iot/sceneRule/' + id,
    method: 'get'
  });
};

/**
 * 新增场景联动
 * @param data
 */
export const addSceneRule = (data: SceneRuleForm) => {
  return request({
    url: '/iot/sceneRule',
    method: 'post',
    data: data
  });
};

/**
 * 修改场景联动
 * @param data
 */
export const updateSceneRule = (data: SceneRuleForm) => {
  return request({
    url: '/iot/sceneRule',
    method: 'put',
    data: data
  });
};

/**
 * 删除场景联动
 * @param id
 */
export const delSceneRule = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/sceneRule/' + id,
    method: 'delete'
  });
};
