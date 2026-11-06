import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { SipConfigVO, SipConfigForm, SipConfigQuery } from '@/api/iot/sipConfig/types';

/**
 * 查询sip系统配置列表
 * @param query
 * @returns {*}
 */

export const listSipConfig = (query?: SipConfigQuery): AxiosPromise<SipConfigVO[]> => {
  return request({
    url: '/iot/sipConfig/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询sip系统配置详细
 * @param id
 */
export const getSipConfig = (id: string | number): AxiosPromise<SipConfigVO> => {
  return request({
    url: '/iot/sipConfig/' + id,
    method: 'get'
  });
};

/**
 * 新增sip系统配置
 * @param data
 */
export const addSipConfig = (data: SipConfigForm) => {
  return request({
    url: '/iot/sipConfig',
    method: 'post',
    data: data
  });
};

/**
 * 修改sip系统配置
 * @param data
 */
export const updateSipConfig = (data: SipConfigForm) => {
  return request({
    url: '/iot/sipConfig',
    method: 'put',
    data: data
  });
};

/**
 * 删除sip系统配置
 * @param id
 */
export const delSipConfig = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/sipConfig/' + id,
    method: 'delete'
  });
};
