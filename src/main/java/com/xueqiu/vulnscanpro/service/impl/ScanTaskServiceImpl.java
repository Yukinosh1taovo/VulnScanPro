package com.xueqiu.vulnscanpro.service.impl;

import com.xueqiu.vulnscanpro.engine.ScanEngine;
import com.xueqiu.vulnscanpro.mapper.ScanTaskMapper;
import com.xueqiu.vulnscanpro.model.dto.request.ScanTaskRequest;
import com.xueqiu.vulnscanpro.model.entity.ScanTask;
import com.xueqiu.vulnscanpro.service.IScanTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class ScanTaskServiceImpl implements IScanTaskService {

    private final ScanTaskMapper scanTaskMapper;

    private final ScanEngine scanEngine; // 扫描引擎注入


    // 创建新扫描任务业务逻辑
    @Override
    public ScanTask createTask(ScanTaskRequest request, Long userId) {

        ScanTask task = new ScanTask();

        // 1. 从 DTO 拷贝用户输入的字段
        task.setTaskName(request.getTaskName());
        task.setTarget(request.getTarget());
        task.setScanPolicy(request.getScanPolicy());

        // 2. 后端强制初始化业务逻辑字段
        task.setUserId(userId);  // 绑定当前登录用户
        task.setStatus("PENDING");  // 强制初始状态为等待
        task.setProgress((byte) 0);  // 强制初始进度为 0
        task.setCreatedAt(LocalDateTime.now());

        // 3. 插入新任务到数据库表
        scanTaskMapper.insert(task); // 这一步由mybatis自动将插入后自增生成的id值返回给task 不需要手动set

        // 校验target是否合法?


        // 4. 触发异步扫描
        scanEngine.startScan(task.getId(), task.getTarget(), task.getScanPolicy());

        return task;

    }


    @Override
    public List<ScanTask> getTasksByUserId(Long userId) {
        return scanTaskMapper.selectByUserId(userId);
    }

    @Override
    public ScanTask getTaskDetailsSecurity(Long taskId, Long userId) {
        return null;
    }

    @Override
    public void deleteTaskSecurity(Long taskId, Long userId) {
        scanTaskMapper.deleteById(taskId);

    }


}
