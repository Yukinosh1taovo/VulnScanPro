package com.xueqiu.vulnscanpro.model.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

// 任务状态日志表（用于详细追踪）
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusLogs {
    private Long id;
    private Long taskId; // 关联的任务ID
    private String stage; // 当前阶段：PORT_SCAN, WEB_CRAWL, VULN_SCAN等
    private String status;// 阶段状态：START, SUCCESS, WARNING, ERROR
    private String message;// 状态详细信息或错误日志
    private Timestamp createdAt; // 日志记录时间
}
