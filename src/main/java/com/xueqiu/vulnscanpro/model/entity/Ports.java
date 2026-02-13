package com.xueqiu.vulnscanpro.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 端口表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ports {
    private Long id;
    private Long asset_id; // 所属资产ID
    private Integer portNumber; // 端口号
    private String protocol; // 协议：tcp, udp
    private String serviceName; // 服务名称（如ssh, http）
    private String versionInfo; // 服务版本信息
    private String state; // 端口状态：open, closed, filtered, unknown
}
