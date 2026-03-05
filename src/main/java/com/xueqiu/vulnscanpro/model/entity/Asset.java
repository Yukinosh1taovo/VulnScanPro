package com.xueqiu.vulnscanpro.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 资产（发现的主机）
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    private Long id;
    private Long taskId; // 所属扫描任务ID
    private String ipAddress; // IP地址（支持IPv6）
    private String hostname; // 主机名
    private String osGuess; // 猜测的操作系统
    private LocalDateTime lastSeen; // 最后一次被发现的时间

}
