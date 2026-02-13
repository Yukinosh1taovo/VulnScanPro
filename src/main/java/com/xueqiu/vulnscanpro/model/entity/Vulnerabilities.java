package com.xueqiu.vulnscanpro.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

// 漏洞表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vulnerabilities {
    private Long id;
    private Long taskId; // 发现该漏洞的任务ID
    private Long assetId;// 关联的资产ID（可为空，如纯域名漏洞）
    private Long portId;// 关联的端口ID（可为空）
    private String vulnType; // 漏洞类型：SQLi, XSS, WEAK_PASSWORD等
    private String title;// 漏洞标题
    private String riskLevel;// 风险等级：CRITICAL, HIGH, MEDIUM, LOW, INFO
    private Boolean isFixed;// 是否已修复（0未修复，1已修复）
    private Timestamp discoveredAt;// 漏洞发现时间
}
