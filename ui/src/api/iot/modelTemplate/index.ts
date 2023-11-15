import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ModelTemplateVO, ModelTemplateForm, ModelTemplateQuery } from '@/api/iot/modelTemplate/types';

/**
 * 查询物模型模板列表
 * @param query
 * @returns {*}
 */

export const listModelTemplate = (query?: ModelTemplateQuery): AxiosPromise<ModelTemplateVO[]> => {
  return request({
    url: '/iot/modelTemplate/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询物模型模板详细
 * @param templateId
 */
export const getModelTemplate = (templateId: string | number): AxiosPromise<ModelTemplateVO> => {
  return request({
    url: '/iot/modelTemplate/' + templateId,
    method: 'get'
  });
};

/**
 * 新增物模型模板
 * @param data
 */
export const addModelTemplate = (data: ModelTemplateForm) => {
  return request({
    url: '/iot/modelTemplate',
    method: 'post',
    data: data
  });
};

/**
 * 修改物模型模板
 * @param data
 */
export const updateModelTemplate = (data: ModelTemplateForm) => {
  return request({
    url: '/iot/modelTemplate',
    method: 'put',
    data: data
  });
};

/**
 * 删除物模型模板
 * @param templateId
 */
export const delModelTemplate = (templateId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/modelTemplate/' + templateId,
    method: 'delete'
  });
};
