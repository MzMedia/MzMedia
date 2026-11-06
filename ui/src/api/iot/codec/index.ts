import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CodecVO, CodecForm, CodecQuery } from '@/api/iot/codec/types';

/**
 * 查询协议编解码器列表
 * @param query
 * @returns {*}
 */

export const listCodec = (query?: CodecQuery): AxiosPromise<CodecVO[]> => {
  return request({
    url: '/iot/codec/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询协议编解码器详细
 * @param id
 */
export const getCodec = (id: string | number): AxiosPromise<CodecVO> => {
  return request({
    url: '/iot/codec/' + id,
    method: 'get'
  });
};

/**
 * 新增协议编解码器
 * @param data
 */
export const addCodec = (data: CodecForm) => {
  return request({
    url: '/iot/codec',
    method: 'post',
    data: data
  });
};

/**
 * 修改协议编解码器
 * @param data
 */
export const updateCodec = (data: CodecForm) => {
  return request({
    url: '/iot/codec',
    method: 'put',
    data: data
  });
};

/**
 * 删除协议编解码器
 * @param id
 */
export const delCodec = (id: string | number | Array<string | number>) => {
  return request({
    url: '/iot/codec/' + id,
    method: 'delete'
  });
};
