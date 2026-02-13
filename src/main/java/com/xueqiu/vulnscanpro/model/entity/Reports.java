package com.xueqiu.vulnscanpro.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

// 报告表
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reports {
    private Long id;
    private Long taskId; // 关联的扫描任务ID
    private Long userId; // 报告生成者ID
    private String title; // 报告标题
    private String summery; // 报告摘要JSON（包含统计信息）
    private String filePath; // 生成的报告文件（PDF/HTML）存储路径
    private Timestamp generatedAt; // 报告生成时间
}
