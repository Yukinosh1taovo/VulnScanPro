package com.xueqiu.vulnscanpro.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value ("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationInMs;

    private Key key;

    @PostConstruct
    public void init(){

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        log.info("原始密钥字节长度: {} 位 ({}字节)", keyBytes.length * 8, keyBytes.length);
        if (keyBytes.length < 64) { // HS512需要至少64字节
            log.error("密钥长度不足！需要至少64字节(512位)，当前仅{}字节。", keyBytes.length);
            // 方案1: 抛异常让应用启动失败
            throw new IllegalArgumentException("JWT密钥长度不足，请使用至少64字节的Base64密钥");
        }




        // 将密钥字符串转换为Key对象
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        log.info("JWT密钥初始化成功，密钥长度: {} 位", keyBytes.length * 8);
    }


    /**
     * 根据认证信息生成JWT令牌
     */
    public String generateToken(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // 将权限列表转换为逗号分隔的字符串
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("authorities",authorities) // 自定义声明：权限
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 从 Token 中提取用户名
     */
    public String getUsernameFromToken(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * 验证JWT令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("无效的JWT签名", e);
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期", e);
        } catch (UnsupportedJwtException e) {
            log.error("不支持的JWT令牌", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims字符串为空", e);
        } catch (Exception e) {
            log.error("验证JWT令牌时发生错误", e);
        }
        return false;
    }

    public long getJwtExpirationInMs(){
        return jwtExpirationInMs;
    }

}
