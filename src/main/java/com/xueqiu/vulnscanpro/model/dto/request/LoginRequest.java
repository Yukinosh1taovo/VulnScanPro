package com.xueqiu.vulnscanpro.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;



/*    注释验证码校验逻辑 方便测试
    @NotBlank(message = "请输入验证码")
    private String captchaCode;  // 用户输入的验证码

    private String captchaUuid;  // 获取图片时拿到的 ID

    */

}
