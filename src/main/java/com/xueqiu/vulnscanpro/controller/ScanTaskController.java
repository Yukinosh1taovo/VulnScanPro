package com.xueqiu.vulnscanpro.controller;


import com.xueqiu.vulnscanpro.model.dto.request.ScanTaskRequest;
import com.xueqiu.vulnscanpro.model.entity.ApiResponse;
import com.xueqiu.vulnscanpro.model.entity.CustomUserDetails;
import com.xueqiu.vulnscanpro.model.entity.ScanTask;
import com.xueqiu.vulnscanpro.service.IScanTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class ScanTaskController {

    private final IScanTaskService scanTaskService;

    // 根据token获取当前用户id
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

//        System.out.println(principal);

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getId();
        }

        // 如果是匿名用户或其他情况
        log.error("当前登录用户类型异常: {}", principal.getClass().getName());
        return null;
    }


    // 创建新扫描任务
    @PostMapping
    public ApiResponse createTask(@Valid @RequestBody ScanTaskRequest request){

        log.info("=== 接收到创建任务请求  任务名: {} ===", request.getTaskName());

        ScanTask newTask = scanTaskService.createTask(request, getCurrentUserId());

        return ApiResponse.success("任务已提交，正在排队中", newTask);
    }


    // 查询当前用户的所有扫描任务
    @GetMapping
    public ApiResponse listTasks(){
        log.info("=== 接收到根据id查询任务列表请求: {} ===", getCurrentUserId());
        return ApiResponse.success(scanTaskService.getTasksByUserId(getCurrentUserId()));
    }


    // 安全获取对应id扫描任务的详情
    @GetMapping("/{id}")
    public ApiResponse getTask(@PathVariable Long id){
        try {
            return ApiResponse.success(scanTaskService.getTaskDetailsSecurity(id,getCurrentUserId()));
        }
        catch (RuntimeException e){
            return ApiResponse.error(e.getMessage());
        }
    }


    // 安全删除扫描任务
    @DeleteMapping("/{id}")
    public ApiResponse deleteTask(@PathVariable Long id){
        try {
            scanTaskService.deleteTaskSecurity(id, getCurrentUserId());
            return ApiResponse.success("删除成功");
        }
        catch (RuntimeException e){
            return ApiResponse.error(e.getMessage());
        }
    }





}
