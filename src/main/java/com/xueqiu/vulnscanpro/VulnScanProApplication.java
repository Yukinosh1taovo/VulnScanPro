package com.xueqiu.vulnscanpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync // 开启异步支持
@SpringBootApplication  // exclude = {SecurityAutoConfiguration.class} 禁用security
public class VulnScanProApplication {
    public static void main(String[] args) {
        SpringApplication.run(VulnScanProApplication.class, args);
    }

}
