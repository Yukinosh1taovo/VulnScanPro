package com.xueqiu.vulnscanpro.model.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


// 扫描任务dto请求类
@Data
public class ScanTaskRequest {

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称不能超过100个字符")
    private String taskName;

    @NotBlank(message = "扫描目标不能为空")
    private String target; // 支持 IP、域名、URL

    @NotBlank(message = "扫描策略不能为空")
    @Pattern(regexp = "^(QUICK|STANDARD|DEEP|STEALTH)$", message = "无效的扫描策略")
    private String scanPolicy = "STANDARD";  // 默认全量扫描


}
