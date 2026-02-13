package com.xueqiu.vulnscanpro.service;

import com.xueqiu.vulnscanpro.model.dto.request.LoginRequest;
import com.xueqiu.vulnscanpro.model.dto.request.RegisterRequest;
import com.xueqiu.vulnscanpro.model.dto.response.LoginResponse;
import com.xueqiu.vulnscanpro.model.entity.User;

public interface IAuthService {

    User register(RegisterRequest request);

    LoginResponse login(LoginRequest request);


}
