package com.wms.config;

import com.wms.common.UnauthorizedException;
import com.wms.entity.SysUser;
import com.wms.mapper.SysUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易 Token 鉴权拦截器：
 * 登录成功后生成 token（UUID），token -> userId 保存在内存中；
 * 前端通过 Authorization: Bearer <token> 携带。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /** token -> 用户 id（演示用内存存储；生产环境应使用 Redis/JWT） */
    private static final Map<String, String> TOKEN_STORE = new ConcurrentHashMap<>();

    private final SysUserMapper sysUserMapper;

    public AuthInterceptor(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }

    public static String createToken(String userId) {
        String token = java.util.UUID.randomUUID().toString().replace("-", "");
        TOKEN_STORE.put(token, userId);
        return token;
    }

    public static void removeToken(String token) {
        if (token != null) {
            TOKEN_STORE.remove(token);
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("未登录或登录已过期");
        }
        String token = authHeader.substring(7).trim();
        String userId = TOKEN_STORE.get(token);
        if (userId == null) {
            throw new UnauthorizedException("登录状态无效，请重新登录");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new UnauthorizedException("用户不存在");
        }
        request.setAttribute("currentUser", user);
        return true;
    }
}
