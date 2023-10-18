package com.mz.iot.service;

import com.mz.iot.domain.SipConfig;
import com.mz.iot.domain.vo.SipConfigVo;
import com.mz.iot.domain.bo.SipConfigBo;
import com.mz.common.mybatis.core.page.TableDataInfo;
import com.mz.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * sip系统配置Service接口
 *
 * @author MzMedia
 * @date 2023-10-18
 */
public interface ISipConfigService {

    /**
     * 查询sip系统配置
     */
    SipConfigVo queryById(Long id);

    /**
     * 查询sip系统配置列表
     */
    TableDataInfo<SipConfigVo> queryPageList(SipConfigBo bo, PageQuery pageQuery);

    /**
     * 查询sip系统配置列表
     */
    List<SipConfigVo> queryList(SipConfigBo bo);

    /**
     * 新增sip系统配置
     */
    Boolean insertByBo(SipConfigBo bo);

    /**
     * 修改sip系统配置
     */
    Boolean updateByBo(SipConfigBo bo);

    /**
     * 校验并批量删除sip系统配置信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
