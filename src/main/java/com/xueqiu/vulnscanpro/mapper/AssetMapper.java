package com.xueqiu.vulnscanpro.mapper;

import com.xueqiu.vulnscanpro.model.entity.Asset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;



@Mapper
public interface AssetMapper {


    /**
     * 根据扫描任务id查询对应资产
     * @param taskId
     * @return
     */
    List<Asset> selectByTaskId(@Param("taskId") Long taskId);


    /**
     * 插入资产信息，并回填自增 ID
     */
    int insert(Asset asset);


}
