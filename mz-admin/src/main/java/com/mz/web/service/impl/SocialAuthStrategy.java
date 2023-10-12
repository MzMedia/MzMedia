package com.mz.web.service.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import com.mz.common.core.constant.Constants;
import com.mz.common.core.domain.model.LoginBody;
import com.mz.common.core.domain.model.LoginUser;
import com.mz.common.core.enums.UserStatus;
import com.mz.common.core.exception.ServiceException;
import com.mz.common.core.exception.user.UserException;
import com.mz.common.core.utils.MessageUtils;
import com.mz.common.core.utils.ValidatorUtils;
import com.mz.common.core.validate.auth.SocialGroup;
import com.mz.common.satoken.utils.LoginHelper;
import com.mz.common.social.config.properties.SocialProperties;
import com.mz.common.social.utils.SocialUtils;
import com.mz.common.tenant.helper.TenantHelper;
import com.mz.system.domain.SysClient;
import com.mz.system.domain.SysUser;
import com.mz.system.domain.vo.SysSocialVo;
import com.mz.system.domain.vo.SysUserVo;
import com.mz.system.mapper.SysUserMapper;
import com.mz.system.service.ISysSocialService;
import com.mz.web.domain.vo.LoginVo;
import com.mz.web.service.IAuthStrategy;
import com.mz.web.service.SysLoginService;
import org.springframework.stereotype.Service;

/**
 * 第三方授权策略
 *
 * @author thiszhc is 三三
 */
@Slf4j
@Service("social" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class SocialAuthStrategy implements IAuthStrategy {

    private final SocialProperties socialProperties;
    private final ISysSocialService sysSocialService;
    private final SysUserMapper userMapper;
    private final SysLoginService loginService;


    @Override
    public void validate(LoginBody loginBody) {
        ValidatorUtils.validate(loginBody, SocialGroup.class);
    }

    /**
     * 登录-第三方授权登录
     *
     * @param clientId  客户端id
     * @param loginBody 登录信息
     * @param client    客户端信息
     */
    @Override
    public LoginVo login(String clientId, LoginBody loginBody, SysClient client) {
        AuthResponse<AuthUser> response = SocialUtils.loginAuth(loginBody, socialProperties);
        if (!response.ok()) {
            throw new ServiceException(response.getMsg());
        }
        AuthUser authUserData = response.getData();
        if ("GITEE".equals(authUserData.getSource())) {
            // 如用户使用 gitee 登录顺手 star 给作者一点支持 拒绝白嫖
//            HttpUtil.createRequest(Method.PUT, "https://gitee.com/api/v5/user/starred/dromara/RuoYi-Vue-Plus")
//                .formStr(MapUtil.of("access_token", authUserData.getToken().getAccessToken()))
//                .executeAsync();

        }

        SysSocialVo social = sysSocialService.selectByAuthId(authUserData.getSource() + authUserData.getUuid());
        if (!ObjectUtil.isNotNull(social)) {
            throw new ServiceException("你还没有绑定第三方账号，绑定后才可以登录！");
        }
        // 验证授权表里面的租户id是否包含当前租户id
        String tenantId = social.getTenantId();
        if (ObjectUtil.isNotNull(social) && StrUtil.isNotBlank(tenantId)
            && !tenantId.contains(loginBody.getTenantId())) {
            throw new ServiceException("对不起，你没有权限登录当前租户！");
        }

        // 查找用户
        SysUserVo user = loadUser(tenantId, social.getUserId());

        // 此处可根据登录用户的数据不同 自行创建 loginUser 属性不够用继承扩展就行了
        LoginUser loginUser = loginService.buildLoginUser(user);
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, clientId);
        // 生成token
        LoginHelper.login(loginUser, model);

        loginService.recordLogininfor(loginUser.getTenantId(), user.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        loginService.recordLoginInfo(user.getUserId());

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(clientId);
        return loginVo;
    }

    private SysUserVo loadUser(String tenantId, Long userId) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .select(SysUser::getUserName, SysUser::getStatus)
            .eq(TenantHelper.isEnable(), SysUser::getTenantId, tenantId)
            .eq(SysUser::getUserId, userId));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", "");
            throw new UserException("user.not.exists", "");
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", "");
            throw new UserException("user.blocked", "");
        }
        if (TenantHelper.isEnable()) {
            return userMapper.selectTenantUserByUserName(user.getUserName(), tenantId);
        }
        return userMapper.selectUserByUserName(user.getUserName());
    }

}
