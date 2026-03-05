package com.xueqiu.vulnscanpro.service.impl;

import com.xueqiu.vulnscanpro.mapper.AssetMapper;
import com.xueqiu.vulnscanpro.mapper.ScanTaskMapper;
import com.xueqiu.vulnscanpro.model.entity.Asset;
import com.xueqiu.vulnscanpro.model.entity.ScanTask;
import com.xueqiu.vulnscanpro.service.IAssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;



@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements IAssetService {

    private final AssetMapper assetMapper;
    private final ScanTaskMapper scanTaskMapper; // 用于权限校验

    @Override
    public List<Asset> getAssetsByTaskId(Long taskId, Long userId) {

        // 1. 安全校验：检查该任务是否属于该用户
        ScanTask task = scanTaskMapper.selectById(taskId);

        if (task == null || !task.getUserId().equals(userId)){
            throw new RuntimeException("权限不足或任务不存在");
        }

        // 2. 执行查询
        return assetMapper.selectByTaskId(taskId);

    }
}
