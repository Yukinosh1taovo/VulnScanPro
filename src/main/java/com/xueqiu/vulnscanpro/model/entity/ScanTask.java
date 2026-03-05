package com.xueqiu.vulnscanpro.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 扫描任务实体类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanTask {
    private Long id;
    private Long userId; // 关联任务创建者ID
    private String taskName; // 任务名称
    private String target; // 扫描目标（IP、域名、URL，支持逗号分隔）
    private String scanPolicy; // 扫描策略：QUICK, FULL, WEB_ONLY等
    private String status; // 任务状态：PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    private Byte progress; // 任务进度（0-100）
    private LocalDateTime createdAt; // 任务创建时间
    private LocalDateTime startTime; // 任务开始执行时间
    private LocalDateTime endTime; // 任务结束时间


}
