package com.xueqiu.vulnscanpro.controller;

import com.xueqiu.vulnscanpro.model.dto.request.LoginRequest;
import com.xueqiu.vulnscanpro.model.dto.request.RegisterRequest;
import com.xueqiu.vulnscanpro.model.dto.response.LoginResponse;
import com.xueqiu.vulnscanpro.model.entity.ApiResponse;
import com.xueqiu.vulnscanpro.model.entity.User;
import com.xueqiu.vulnscanpro.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;


    @PostMapping("/register")
    public ApiResponse register(@RequestBody RegisterRequest request){

        log.info("=== 开始处理注册请求 ===");
        log.info("注册请求:{}",request);

        try {
            User user = authService.register(request);
            log.info("新用户注册成功，id:{}", user.getId());
            return ApiResponse.success(user);
        }
        catch (Exception e){
            log.error("注册失败", e);
            return ApiResponse.error(e.getMessage());
        }

    }


    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest loginRequest){
        log.info("=== 开始处理登录请求 ===");
        log.info("登录请求:{}", loginRequest);

        // 调用Service进行认证并获取令牌
        LoginResponse loginResponse = authService.login(loginRequest);

        // 使用统一的成功响应格式返回
        return ApiResponse.success("登录成功",loginResponse);
    }



}
