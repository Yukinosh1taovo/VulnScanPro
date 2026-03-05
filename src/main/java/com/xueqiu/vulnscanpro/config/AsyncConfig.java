package com.xueqiu.vulnscanpro.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


// 创建 AsyncConfig 配置类 配置线程池，使用 @EnableAsync 和 @Configuration。
// 定义线程池 (ThreadPoolTaskExecutor)，用于执行耗时的扫描任务，避免阻塞HTTP请求线程。
@Configuration
public class AsyncConfig {
    @Bean(name = "scanExecutor")
    public Executor scanExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // 核心线程数
        executor.setMaxPoolSize(10);  // 最大并发扫描数
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("NmapScan-");
        executor.initialize();
        return executor;
    }
}
