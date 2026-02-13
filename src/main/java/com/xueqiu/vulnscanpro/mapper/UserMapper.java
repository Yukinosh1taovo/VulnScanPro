package com.xueqiu.vulnscanpro.mapper;

import com.xueqiu.vulnscanpro.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;


@Mapper
public interface UserMapper {

    /**
     * 插入新用户
     * @param user 用户对象
     * @return 受影响的行数
     */
    int insert(User user);


    /**
     * 根据用户名查询用户 登录时凭用户名查询用户
     * @param username 用户名
     * @return 用户对象，未找到时返回null
     */
    User selectByUsername(@Param("username") String username);



    /**
     * 根据ID查询用户 用于服务层获取用户详情
     * @param id 用户id
     * @return 用户对象，未找到时返回null
     */
    User selectById(@Param("id") Long id);


    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return 存在的数量
     */
    int countByUsername(@Param("username") String username);


    /**
     * 通用更新，选择性更新用户信息
     * @param user
     * @return
     */
//    int updateById(@Param("user") User user);


    int updateLastLoginTime(@Param("id") Long id, @Param("loginTime") LocalDateTime loginTime);





}
