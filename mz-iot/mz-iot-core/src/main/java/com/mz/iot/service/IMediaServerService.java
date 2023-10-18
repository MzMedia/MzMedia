package com.mz.iot.service;

import com.mz.iot.domain.MediaServer;
import com.mz.iot.domain.vo.MediaServerVo;
import com.mz.iot.domain.bo.MediaServerBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 流媒体服务器配置Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface IMediaServerService {

    /**
     * 查询流媒体服务器配置
     */
    MediaServerVo queryById(Long id);

    /**
     * 查询流媒体服务器配置列表
     */
    TableDataInfo<MediaServerVo> queryPageList(MediaServerBo bo, PageQuery pageQuery);

    /**
     * 查询流媒体服务器配置列表
     */
    List<MediaServerVo> queryList(MediaServerBo bo);

    /**
     * 新增流媒体服务器配置
     */
    Boolean insertByBo(MediaServerBo bo);

    /**
     * 修改流媒体服务器配置
     */
    Boolean updateByBo(MediaServerBo bo);

    /**
     * 校验并批量删除流媒体服务器配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
