package com.xueqiu.vulnscanpro.service.impl;

import com.xueqiu.vulnscanpro.mapper.UserMapper;
import com.xueqiu.vulnscanpro.model.dto.request.LoginRequest;
import com.xueqiu.vulnscanpro.model.dto.request.RegisterRequest;
import com.xueqiu.vulnscanpro.model.dto.response.LoginResponse;
import com.xueqiu.vulnscanpro.model.entity.User;
import com.xueqiu.vulnscanpro.service.IAuthService;
import com.xueqiu.vulnscanpro.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor // lombok构造器注入依赖
public class AuthServiceImpl implements IAuthService {

    private final UserMapper userMapper; // MyBatis Mapper
    private final PasswordEncoder passwordEncoder;   // 用于明文密码加密
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    // 注册业务逻辑实现
    @Override
    @Transactional
    public User register(RegisterRequest request) {
        // 1. 检查用户名是否存在
        if(userMapper.countByUsername(request.getUsername()) > 0){
            throw new RuntimeException("用户名已经存在");
        }

        // 2. 创建并保存用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword())); // 加密密码存入
        user.setEmail(request.getEmail());
        user.setRole("USER");
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        // 3. 保存到数据库
        userMapper.insert(user);

        log.info("用户注册成功: {}", user.getUsername());

        // 4. 返回用户信息（不包含密码）
        user.setPasswordHash(null);
        return user;
    }


    // 登录业务逻辑实现
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        log.info("尝试登录用户:{}", username);


        // 1. 使用Spring Security进行身份认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // 2. 将认证信息设置到安全上下文中
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 生成JWT令牌
        String jwt = jwtTokenProvider.generateToken(authentication);
        log.info("用户 {} 认证成功, 令牌已生成", username);

        // 4. 获取用户详细信息，用于构建响应
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            // 理论上不会发生，因为认证已通过，但为了健壮性保留检查
            throw new RuntimeException("用户不存在");
        }

        // 记录用户上次登录时间
        LocalDateTime lastLoginTime = user.getLastLoginAt();

        // 更新用户登录时间
        userMapper.updateLastLoginTime(user.getId(), LocalDateTime.now());

        // 5. 构建并返回登录响应
        return LoginResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .expiresIn(System.currentTimeMillis() + jwtTokenProvider.getJwtExpirationInMs())
                .userInfo(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .lastLoginTime(lastLoginTime)
                        .build())
                .build();
    }
}
