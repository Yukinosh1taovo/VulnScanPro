package com.xueqiu.vulnscanpro.config;

import com.xueqiu.vulnscanpro.filter.JwtAuthenticationFilter;
import com.xueqiu.vulnscanpro.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity // 该注解启用 Spring Security 的 web 安全功能。
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    /**
     *
     * 配置http安全规则
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF、CORS、表单登录、HTTP Basic 认证（API项目通常不需要）
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 配置会话管理为无状态（适合前后端分离）
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置授权规则
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/**").permitAll()  // 测试暂时允许所有接口访问

                   /*

                    // 公开访问的接口（不需要登录）
                    .requestMatchers("/api/auth/**").permitAll()  // 放行认证相关接口
                    // 其他所有请求都需要认证
                    .anyRequest().authenticated()

                    */
            )


                // 添加jwt过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // ✅ 添加异常处理
                .exceptionHandling(ex -> ex
                        // 未认证时返回 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"未登录，请先登录\"}");
                        })
                        // 没有权限时返回 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"error\":\"权限不足\"}");
                        })
                );

        return http.build();
    }


    /**
     *
     * 密码加密器配置
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // 使用BCrypt强哈希算法加密密码
        return new BCryptPasswordEncoder();
    }



    /**
     * 配置用户信息（内存方式，仅用于测试）
     */
/*    @Bean
    public UserDetailsService userDetailsService() {
        // 创建普通用户
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder().encode("123456"))
                .roles("USER")  // 角色：USER
                .build();

        // 创建管理员
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN", "USER")  // 角色：ADMIN 和 USER
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }*/



    /**
     * AuthenticationManager Bean（登录时需要用到）
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }



}
