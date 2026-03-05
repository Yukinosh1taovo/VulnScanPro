package com.xueqiu.vulnscanpro.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.UUID;
import com.xueqiu.vulnscanpro.model.dto.request.LoginRequest;
import com.xueqiu.vulnscanpro.model.dto.request.RegisterRequest;
import com.xueqiu.vulnscanpro.model.dto.response.LoginResponse;
import com.xueqiu.vulnscanpro.model.entity.ApiResponse;
import com.xueqiu.vulnscanpro.model.entity.User;
import com.xueqiu.vulnscanpro.service.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    public static final Map<String, String> CAPTCHA_STORE = new ConcurrentHashMap<>();


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


        // 先注释验证码校验逻辑 方便测试
/*
        // 1. === 校验验证码逻辑 ===
        String uuid = loginRequest.getCaptchaUuid();
        String userInputCode = loginRequest.getCaptchaCode();

        // 从存储中取出正确答案
        String correctCode = CAPTCHA_STORE.get(uuid);

        if (correctCode == null) {
            return ApiResponse.error("验证码已过期，请刷新");
        }

        // 校验（不区分大小写）
        if (!correctCode.equalsIgnoreCase(userInputCode)) {
            return ApiResponse.error("验证码错误");
        }

        // 验证通过后，记得移除这个 Key，防止重复使用
        CAPTCHA_STORE.remove(uuid);

        */

        try {
            // 调用Service进行认证并获取令牌
            LoginResponse loginResponse = authService.login(loginRequest);

            // 使用统一的成功响应格式返回
            return ApiResponse.success("登录成功",loginResponse);

        }
        catch (Exception e){
            log.error("登录失败");
            return ApiResponse.error("账号或密码错误");

        }
    }


    // 获取验证码接口
    @GetMapping("/captcha")
    public ApiResponse getCaptcha(){
        // 1. 生成线段干扰的验证码，宽100，高40
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(100,40);

        // 2. 获取验证码的字符（例如 "A1B2"）
        String code = lineCaptcha.getCode();

        // 3. 生成一个唯一标识符 (UUID)
        String uuid = UUID.randomUUID().toString();

        // 4. 存入 Store (如果是 Redis，设置过期时间比如 2 分钟)
        CAPTCHA_STORE.put(uuid,code);

        // 5. 将图片转为 Base64
        String imageBase64 = lineCaptcha.getImageBase64();

        // 6. 返回给前端：UUID 和 Base64图片
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("image", "data:image/png;base64," + imageBase64); // 前端可以直接放到 src 里

        return ApiResponse.success(result);

    }





}
