package com.xueqiu.vulnscanpro.mapper;

import com.xueqiu.vulnscanpro.model.entity.ScanTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface ScanTaskMapper {

    /**
     * 查询指定用户的所有扫描任务（按创建时间倒序）
     */
    List<ScanTask> selectByUserId(@Param("userId") Long userId);


    /**
     * 插入新任务
     * 执行后，自增 ID 会自动填充到 task 对象的 id 属性中
     */
    int insert(ScanTask task);


    /**
     * 根据 ID 查询任务详情
     */
    ScanTask selectById(@Param("id") Long id);


    /**
     * 安全删除任务
     */
    int deleteById(@Param("id") Long id);


    /**
     * 更新任务状态或进度（供扫描引擎调用）
     */
    int updateTaskStatus(ScanTask task);


}
