package com.xueqiu.vulnscanpro.service.impl;

import com.xueqiu.vulnscanpro.mapper.UserMapper;
import com.xueqiu.vulnscanpro.model.entity.CustomUserDetails;
import com.xueqiu.vulnscanpro.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userMapper.selectByUsername(username);

        log.info("从数据库查出的用户实体: id={}, username={}", user.getId(), user.getUsername());

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 转换为 自定义的CustomUserDetails
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getIsActive(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
