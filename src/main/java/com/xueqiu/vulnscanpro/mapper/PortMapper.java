package com.xueqiu.vulnscanpro.mapper;

import com.xueqiu.vulnscanpro.model.entity.Port;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PortMapper {

    /**
     * 插入端口信息
     */
    int insert(Port port);

}
