package com.mz.web.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mz.common.core.constant.Constants;
import com.mz.common.core.constant.GlobalConstants;
import com.mz.common.core.domain.model.LoginBody;
import com.mz.common.core.domain.model.LoginUser;
import com.mz.common.core.enums.LoginType;
import com.mz.common.core.enums.UserStatus;
import com.mz.common.core.exception.user.CaptchaException;
import com.mz.common.core.exception.user.CaptchaExpireException;
import com.mz.common.core.exception.user.UserException;
import com.mz.common.core.utils.MessageUtils;
import com.mz.common.core.utils.StringUtils;
import com.mz.common.core.utils.ValidatorUtils;
import com.mz.common.core.validate.auth.PasswordGroup;
import com.mz.common.redis.utils.RedisUtils;
import com.mz.common.satoken.utils.LoginHelper;
import com.mz.common.tenant.helper.TenantHelper;
import com.mz.common.web.config.properties.CaptchaProperties;
import com.mz.system.domain.SysClient;
import com.mz.system.domain.SysUser;
import com.mz.system.domain.vo.SysUserVo;
import com.mz.system.mapper.SysUserMapper;
import com.mz.web.domain.vo.LoginVo;
import com.mz.web.service.IAuthStrategy;
import com.mz.web.service.SysLoginService;
import org.springframework.stereotype.Service;

/**
 * 密码认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordAuthStrategy implements IAuthStrategy {

    private final CaptchaProperties captchaProperties;
    private final SysLoginService loginService;
    private final SysUserMapper userMapper;

    @Override
    public void validate(LoginBody loginBody) {
        ValidatorUtils.validate(loginBody, PasswordGroup.class);
    }

    @Override
    public LoginVo login(String clientId, LoginBody loginBody, SysClient client) {
        String tenantId = loginBody.getTenantId();
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();
        String code = loginBody.getCode();
        String uuid = loginBody.getUuid();

        boolean captchaEnabled = captchaProperties.getEnable();
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, code, uuid);
        }

        SysUserVo user = loadUserByUsername(tenantId, username);
        loginService.checkLogin(LoginType.PASSWORD, tenantId, username, () -> !BCrypt.checkpw(password, user.getPassword()));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
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

        loginService.recordLogininfor(loginUser.getTenantId(), username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        loginService.recordLoginInfo(user.getUserId());

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(clientId);
        return loginVo;
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    private void validateCaptcha(String tenantId, String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    private SysUserVo loadUserByUsername(String tenantId, String username) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
            .select(SysUser::getUserName, SysUser::getStatus)
            .eq(TenantHelper.isEnable(), SysUser::getTenantId, tenantId)
            .eq(SysUser::getUserName, username));
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UserException("user.blocked", username);
        }
        if (TenantHelper.isEnable()) {
            return userMapper.selectTenantUserByUserName(username, tenantId);
        }
        return userMapper.selectUserByUserName(username);
    }

}
