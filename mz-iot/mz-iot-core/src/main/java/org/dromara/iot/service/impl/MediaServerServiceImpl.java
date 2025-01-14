package org.dromara.iot.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.iot.domain.bo.MediaServerBo;
import org.dromara.iot.domain.vo.MediaServerVo;
import org.dromara.iot.domain.MediaServer;
import org.dromara.iot.mapper.MediaServerMapper;
import org.dromara.iot.service.IMediaServerService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 流媒体服务器配置Service业务层处理
 *
 * @author MzMedia
 * @date 2025-01-14
 */
@RequiredArgsConstructor
@Service
public class MediaServerServiceImpl implements IMediaServerService {

    private final MediaServerMapper baseMapper;

    /**
     * 查询流媒体服务器配置
     *
     * @param id 主键
     * @return 流媒体服务器配置
     */
    @Override
    public MediaServerVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询流媒体服务器配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 流媒体服务器配置分页列表
     */
    @Override
    public TableDataInfo<MediaServerVo> queryPageList(MediaServerBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MediaServer> lqw = buildQueryWrapper(bo);
        Page<MediaServerVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的流媒体服务器配置列表
     *
     * @param bo 查询条件
     * @return 流媒体服务器配置列表
     */
    @Override
    public List<MediaServerVo> queryList(MediaServerBo bo) {
        LambdaQueryWrapper<MediaServer> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MediaServer> buildQueryWrapper(MediaServerBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MediaServer> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getServerId()), MediaServer::getServerId, bo.getServerId());
        lqw.eq(bo.getEnabled() != null, MediaServer::getEnabled, bo.getEnabled());
        lqw.eq(StringUtils.isNotBlank(bo.getProtocol()), MediaServer::getProtocol, bo.getProtocol());
        lqw.eq(StringUtils.isNotBlank(bo.getIp()), MediaServer::getIp, bo.getIp());
        lqw.eq(StringUtils.isNotBlank(bo.getDomain()), MediaServer::getDomain, bo.getDomain());
        lqw.eq(StringUtils.isNotBlank(bo.getHookurl()), MediaServer::getHookurl, bo.getHookurl());
        lqw.eq(StringUtils.isNotBlank(bo.getSecret()), MediaServer::getSecret, bo.getSecret());
        lqw.eq(bo.getPortHttp() != null, MediaServer::getPortHttp, bo.getPortHttp());
        lqw.eq(bo.getPortHttps() != null, MediaServer::getPortHttps, bo.getPortHttps());
        lqw.eq(bo.getPortRtmp() != null, MediaServer::getPortRtmp, bo.getPortRtmp());
        lqw.eq(bo.getPortRtsp() != null, MediaServer::getPortRtsp, bo.getPortRtsp());
        lqw.eq(bo.getRtpProxyPort() != null, MediaServer::getRtpProxyPort, bo.getRtpProxyPort());
        lqw.eq(bo.getPortWs() != null, MediaServer::getPortWs, bo.getPortWs());
        lqw.eq(bo.getRtpEnable() != null, MediaServer::getRtpEnable, bo.getRtpEnable());
        lqw.eq(StringUtils.isNotBlank(bo.getRtpPortRange()), MediaServer::getRtpPortRange, bo.getRtpPortRange());
        lqw.eq(bo.getRecordPort() != null, MediaServer::getRecordPort, bo.getRecordPort());
        lqw.eq(bo.getAutoConfig() != null, MediaServer::getAutoConfig, bo.getAutoConfig());
        lqw.eq(bo.getStatus() != null, MediaServer::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增流媒体服务器配置
     *
     * @param bo 流媒体服务器配置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MediaServerBo bo) {
        MediaServer add = MapstructUtils.convert(bo, MediaServer.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改流媒体服务器配置
     *
     * @param bo 流媒体服务器配置
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MediaServerBo bo) {
        MediaServer update = MapstructUtils.convert(bo, MediaServer.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MediaServer entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除流媒体服务器配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
