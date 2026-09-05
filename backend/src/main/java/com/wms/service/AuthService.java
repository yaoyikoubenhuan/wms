package com.wms.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wms.common.BusinessException;
import com.wms.config.AuthInterceptor;
import com.wms.dto.LoginRequest;
import com.wms.dto.LoginResponse;
import com.wms.entity.SysUser;
import com.wms.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

@Service
public class AuthService {

    private final SysUserMapper sysUserMapper;

    public AuthService(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        String md5Password = DigestUtils.md5DigestAsHex(request.getPassword().getBytes(StandardCharsets.UTF_8));
        if (!md5Password.equalsIgnoreCase(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        String token = AuthInterceptor.createToken(user.getId());
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                user.getId(), user.getUsername(), user.getName(), user.getRole(), user.getAvatar());
        return new LoginResponse(token, userInfo);
    }

    public void logout(String token) {
        AuthInterceptor.removeToken(token);
    }

    public LoginResponse.UserInfo getUserInfo(SysUser user) {
        return new LoginResponse.UserInfo(
                user.getId(), user.getUsername(), user.getName(), user.getRole(), user.getAvatar());
    }
}
