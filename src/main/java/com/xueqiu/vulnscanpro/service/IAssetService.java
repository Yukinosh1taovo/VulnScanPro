package com.xueqiu.vulnscanpro.service;


import com.xueqiu.vulnscanpro.model.entity.Asset;
import java.util.List;


public interface IAssetService {

    List<Asset> getAssetsByTaskId(Long taskId, Long userId);
}
