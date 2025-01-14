package org.dromara.iot.service;

import org.dromara.iot.domain.vo.SipConfigVo;
import org.dromara.iot.domain.bo.SipConfigBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * sip系统配置Service接口
 *
 * @author MzMedia
 * @date 2025-01-14
 */
public interface ISipConfigService {

    /**
     * 查询sip系统配置
     *
     * @param id 主键
     * @return sip系统配置
     */
    SipConfigVo queryById(Long id);

    /**
     * 分页查询sip系统配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return sip系统配置分页列表
     */
    TableDataInfo<SipConfigVo> queryPageList(SipConfigBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的sip系统配置列表
     *
     * @param bo 查询条件
     * @return sip系统配置列表
     */
    List<SipConfigVo> queryList(SipConfigBo bo);

    /**
     * 新增sip系统配置
     *
     * @param bo sip系统配置
     * @return 是否新增成功
     */
    Boolean insertByBo(SipConfigBo bo);

    /**
     * 修改sip系统配置
     *
     * @param bo sip系统配置
     * @return 是否修改成功
     */
    Boolean updateByBo(SipConfigBo bo);

    /**
     * 校验并批量删除sip系统配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
