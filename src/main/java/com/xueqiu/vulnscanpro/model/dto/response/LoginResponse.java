package com.xueqiu.vulnscanpro.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    // JWT访问令牌
    private String accessToken;
    // 令牌类型，通常是 "Bearer"
    private String tokenType = "Bearer";
    // 令牌过期时间（毫秒时间戳）
    private Long expiresIn;
    // 用户基本信息
    private UserInfo userInfo;



    @Data
    @Builder
    public static class UserInfo{
        private Long id;
        private String username;
        private String email;
        private String role;
        private LocalDateTime lastLoginTime;
    }
}
