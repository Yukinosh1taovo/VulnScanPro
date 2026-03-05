package com.xueqiu.vulnscanpro.service;

import com.xueqiu.vulnscanpro.model.dto.request.ScanTaskRequest;
import com.xueqiu.vulnscanpro.model.entity.ScanTask;

import java.util.List;

public interface IScanTaskService {

    // 创建任务
    ScanTask createTask(ScanTaskRequest request, Long userId);

    // 获取当前用户任务列表
    List<ScanTask> getTasksByUserId(Long userId);

    // 安全获取任务详情(含权限校验)
    ScanTask getTaskDetailsSecurity(Long taskId, Long userId);

    // 安全删除任务(含权限校验)
    void deleteTaskSecurity(Long taskId, Long userId);
}
