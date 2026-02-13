-- 1. 用户表
CREATE TABLE `users` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
                         `username` varchar(50) NOT NULL COMMENT '用户名，用于登录',
                         `password_hash` varchar(255) NOT NULL COMMENT '加密后的密码',
                         `email` varchar(100) DEFAULT NULL COMMENT '电子邮箱',
                         `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN, USER',
                         `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '账户是否启用（1启用，0禁用）',
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账户创建时间',
                         `updated_at` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_users_username` (`username`),
                         KEY `idx_users_role` (`role`),
                         KEY `idx_users_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

-- 2. 扫描任务主表
CREATE TABLE `scan_tasks` (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务唯一标识',
                              `user_id` bigint NOT NULL COMMENT '任务创建者ID',
                              `task_name` varchar(100) NOT NULL COMMENT '任务名称',
                              `target` text NOT NULL COMMENT '扫描目标（IP、域名、URL，支持逗号分隔）',
                              `scan_policy` varchar(50) NOT NULL DEFAULT 'FULL' COMMENT '扫描策略：QUICK, FULL, WEB_ONLY等',
                              `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态：PENDING, RUNNING, COMPLETED, FAILED, CANCELLED',
                              `progress` tinyint DEFAULT '0' COMMENT '任务进度（0-100）',
                              `start_time` timestamp NULL DEFAULT NULL COMMENT '任务开始执行时间',
                              `end_time` timestamp NULL DEFAULT NULL COMMENT '任务结束时间',
                              `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '任务创建时间',
                              PRIMARY KEY (`id`),
                              KEY `idx_tasks_user_id` (`user_id`),
                              KEY `idx_tasks_status` (`status`),
                              KEY `idx_tasks_created_at` (`created_at`),
                              CONSTRAINT `fk_tasks_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='扫描任务主表';

-- 3. 任务状态日志表（用于详细追踪）
CREATE TABLE `task_status_logs` (
                                    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
                                    `task_id` bigint NOT NULL COMMENT '关联的任务ID',
                                    `stage` varchar(50) NOT NULL COMMENT '当前阶段：PORT_SCAN, WEB_CRAWL, VULN_SCAN等',
                                    `status` varchar(20) NOT NULL COMMENT '阶段状态：START, SUCCESS, WARNING, ERROR',
                                    `message` text COMMENT '状态详细信息或错误日志',
                                    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '日志记录时间',
                                    PRIMARY KEY (`id`),
                                    KEY `idx_logs_task_id` (`task_id`),
                                    KEY `idx_logs_created_at` (`created_at`),
                                    CONSTRAINT `fk_logs_task_id` FOREIGN KEY (`task_id`) REFERENCES `scan_tasks` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='任务状态详细日志表';

-- 4. 资产表（发现的主机）
CREATE TABLE `assets` (
                          `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资产ID',
                          `task_id` bigint NOT NULL COMMENT '所属扫描任务ID',
                          `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP地址（支持IPv6）',
                          `hostname` varchar(255) DEFAULT NULL COMMENT '主机名',
                          `os_guess` varchar(100) DEFAULT NULL COMMENT '猜测的操作系统',
                          `last_seen` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次被发现的时间',
                          PRIMARY KEY (`id`),
                          KEY `idx_assets_task_id` (`task_id`),
                          KEY `idx_assets_ip` (`ip_address`),
                          CONSTRAINT `fk_assets_task_id` FOREIGN KEY (`task_id`) REFERENCES `scan_tasks` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发现的网络资产表';

-- 5. 端口表
CREATE TABLE `ports` (
                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '端口记录ID',
                         `asset_id` bigint NOT NULL COMMENT '所属资产ID',
                         `port_number` int NOT NULL COMMENT '端口号',
                         `protocol` varchar(10) DEFAULT 'tcp' COMMENT '协议：tcp, udp',
                         `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称（如ssh, http）',
                         `version_info` varchar(255) DEFAULT NULL COMMENT '服务版本信息',
                         `state` varchar(20) NOT NULL DEFAULT 'unknown' COMMENT '端口状态：open, closed, filtered, unknown',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `uk_ports_asset_port` (`asset_id`, `port_number`, `protocol`), -- 防止重复记录
                         KEY `idx_ports_service` (`service_name`),
                         CONSTRAINT `fk_ports_asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产开放端口及服务表';

-- 6. 漏洞表
CREATE TABLE `vulnerabilities` (
                                   `id` bigint NOT NULL AUTO_INCREMENT COMMENT '漏洞记录ID',
                                   `task_id` bigint NOT NULL COMMENT '发现该漏洞的任务ID',
                                   `asset_id` bigint DEFAULT NULL COMMENT '关联的资产ID（可为空，如纯域名漏洞）',
                                   `port_id` bigint DEFAULT NULL COMMENT '关联的端口ID（可为空）',
                                   `vuln_type` varchar(50) NOT NULL COMMENT '漏洞类型：SQLi, XSS, WEAK_PASSWORD等',
                                   `title` varchar(255) NOT NULL COMMENT '漏洞标题',
                                   `risk_level` varchar(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '风险等级：CRITICAL, HIGH, MEDIUM, LOW, INFO',
                                   `is_fixed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已修复（0未修复，1已修复）',
                                   `discovered_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '漏洞发现时间',
                                   PRIMARY KEY (`id`),
                                   KEY `idx_vulns_task_id` (`task_id`),
                                   KEY `idx_vulns_asset_id` (`asset_id`),
                                   KEY `idx_vulns_risk_level` (`risk_level`), -- 便于按风险级别筛选
                                   KEY `idx_vulns_discovered_at` (`discovered_at`),
                                   CONSTRAINT `fk_vulns_task_id` FOREIGN KEY (`task_id`) REFERENCES `scan_tasks` (`id`) ON DELETE CASCADE,
                                   CONSTRAINT `fk_vulns_asset_id` FOREIGN KEY (`asset_id`) REFERENCES `assets` (`id`) ON DELETE SET NULL,
                                   CONSTRAINT `fk_vulns_port_id` FOREIGN KEY (`port_id`) REFERENCES `ports` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漏洞主表';

-- 7. 漏洞详情表（与漏洞表一对一或一对多）
CREATE TABLE `vulnerability_details` (
                                         `id` bigint NOT NULL AUTO_INCREMENT COMMENT '详情ID',
                                         `vulnerability_id` bigint NOT NULL COMMENT '关联的漏洞ID',
                                         `description` text COMMENT '漏洞详细描述',
                                         `proof` text COMMENT '漏洞证据（如请求/响应包）',
                                         `recommendation` text COMMENT '修复建议',
                                         `cve_id` varchar(20) DEFAULT NULL COMMENT '关联的CVE编号（如CVE-2021-12345）',
                                         `scanner_source` varchar(50) DEFAULT NULL COMMENT '发现此漏洞的扫描器或插件名称',
                                         PRIMARY KEY (`id`),
                                         UNIQUE KEY `uk_details_vuln_id` (`vulnerability_id`), -- 确保一个漏洞只有一条详情记录
                                         KEY `idx_details_cve_id` (`cve_id`),
                                         CONSTRAINT `fk_details_vuln_id` FOREIGN KEY (`vulnerability_id`) REFERENCES `vulnerabilities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='漏洞详情表';

-- 8. 报告表
CREATE TABLE `reports` (
                           `id` bigint NOT NULL AUTO_INCREMENT COMMENT '报告ID',
                           `task_id` bigint NOT NULL COMMENT '关联的扫描任务ID',
                           `user_id` bigint NOT NULL COMMENT '报告生成者ID',
                           `title` varchar(255) NOT NULL COMMENT '报告标题',
                           `summary` json DEFAULT NULL COMMENT '报告摘要JSON（包含统计信息）',
                           `file_path` varchar(500) DEFAULT NULL COMMENT '生成的报告文件（PDF/HTML）存储路径',
                           `generated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报告生成时间',
                           PRIMARY KEY (`id`),
                           KEY `idx_reports_task_id` (`task_id`),
                           KEY `idx_reports_user_id` (`user_id`),
                           CONSTRAINT `fk_reports_task_id` FOREIGN KEY (`task_id`) REFERENCES `scan_tasks` (`id`) ON DELETE CASCADE,
                           CONSTRAINT `fk_reports_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='扫描报告表';