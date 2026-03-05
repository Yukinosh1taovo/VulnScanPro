package com.xueqiu.vulnscanpro.controller;


import com.xueqiu.vulnscanpro.model.entity.ApiResponse;
import com.xueqiu.vulnscanpro.model.entity.Asset;
import com.xueqiu.vulnscanpro.model.entity.CustomUserDetails;
import com.xueqiu.vulnscanpro.service.IAssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final IAssetService assetService;

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



    // // 获取当前用户在某个任务下的所有资产
    @GetMapping("/task/{taskId}")
    public ApiResponse getTaskAssets(@PathVariable Long taskId){

        List<Asset> assets = assetService.getAssetsByTaskId(taskId, getCurrentUserId());

        return ApiResponse.success(assets);
    }


}
