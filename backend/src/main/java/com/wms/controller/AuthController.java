package com.wms.controller;

import com.wms.common.Result;
import com.wms.dto.LoginRequest;
import com.wms.dto.LoginResponse;
import com.wms.entity.SysUser;
import com.wms.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authService.logout(authHeader.substring(7).trim());
        }
        return Result.ok();
    }

    @GetMapping("/me")
    public Result<LoginResponse.UserInfo> me(HttpServletRequest request) {
        SysUser user = (SysUser) request.getAttribute("currentUser");
        return Result.ok(authService.getUserInfo(user));
    }
}
