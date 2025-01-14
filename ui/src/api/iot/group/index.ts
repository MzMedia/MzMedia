import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { GroupVO, GroupForm, GroupQuery } from '@/api/iot/group/types';

/**
 * 查询设备分组列表
 * @param query
 * @returns {*}
 */

export const listGroup = (query?: GroupQuery): AxiosPromise<GroupVO[]> => {
  return request({
    url: '/iot/group/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备分组详细
 * @param id
 */
export const getGroup = (id: string | number): AxiosPromise<GroupVO> => {
  return request({
    url: '/iot/group/' + id,
    method: 'get'
  });
};

/**
 * 新增设备分组
 * @param data
 */
export const addGroup = (data: GroupForm) => {
  return request({
    url: '/iot/group',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备分组
 * @param data
 */
export const updateGroup = (data: GroupForm) => {
  return request({
    url: '/iot/group',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备分组
 * @param id
 */
export const delGroup = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/group/' + id,
    method: 'delete'
  });
};
