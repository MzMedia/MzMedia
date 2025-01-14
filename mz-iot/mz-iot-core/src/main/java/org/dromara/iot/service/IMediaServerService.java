package org.dromara.iot.service;

import org.dromara.iot.domain.vo.MediaServerVo;
import org.dromara.iot.domain.bo.MediaServerBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 流媒体服务器配置Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface IMediaServerService {

    /**
     * 查询流媒体服务器配置
     *
     * @param id 主键
     * @return 流媒体服务器配置
     */
    MediaServerVo queryById(Long id);

    /**
     * 分页查询流媒体服务器配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 流媒体服务器配置分页列表
     */
    TableDataInfo<MediaServerVo> queryPageList(MediaServerBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的流媒体服务器配置列表
     *
     * @param bo 查询条件
     * @return 流媒体服务器配置列表
     */
    List<MediaServerVo> queryList(MediaServerBo bo);

    /**
     * 新增流媒体服务器配置
     *
     * @param bo 流媒体服务器配置
     * @return 是否新增成功
     */
    Boolean insertByBo(MediaServerBo bo);

    /**
     * 修改流媒体服务器配置
     *
     * @param bo 流媒体服务器配置
     * @return 是否修改成功
     */
    Boolean updateByBo(MediaServerBo bo);

    /**
     * 校验并批量删除流媒体服务器配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
