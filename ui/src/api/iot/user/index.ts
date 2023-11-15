import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { UserVO, UserForm, UserQuery } from '@/api/iot/user/types';

/**
 * 查询设备用户列表
 * @param query
 * @returns {*}
 */

export const listUser = (query?: UserQuery): AxiosPromise<UserVO[]> => {
  return request({
    url: '/iot/user/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询设备用户详细
 * @param userId
 */
export const getUser = (userId: string | number): AxiosPromise<UserVO> => {
  return request({
    url: '/iot/user/' + userId,
    method: 'get'
  });
};

/**
 * 新增设备用户
 * @param data
 */
export const addUser = (data: UserForm) => {
  return request({
    url: '/iot/user',
    method: 'post',
    data: data
  });
};

/**
 * 修改设备用户
 * @param data
 */
export const updateUser = (data: UserForm) => {
  return request({
    url: '/iot/user',
    method: 'put',
    data: data
  });
};

/**
 * 删除设备用户
 * @param userId
 */
export const delUser = (userId: string | number | Array<string | number>) => {
  return request({
    url: '/iot/user/' + userId,
    method: 'delete'
  });
};
