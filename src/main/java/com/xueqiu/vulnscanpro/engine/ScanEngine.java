package com.xueqiu.vulnscanpro.engine;

import com.xueqiu.vulnscanpro.mapper.AssetMapper;
import com.xueqiu.vulnscanpro.mapper.ScanTaskMapper;
import com.xueqiu.vulnscanpro.model.entity.ScanTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScanEngine {

    private final ScanTaskMapper taskMapper;
    private final NmapResultParser nmapResultParser;

    // 更新当前扫描任务状态
    private void updateTask(Long id, String status, int progress, LocalDateTime time) {
        ScanTask task = new ScanTask();
        task.setId(id);
        task.setStatus(status);
        task.setProgress((byte) progress);
        if(status.equals("RUNNING")) task.setStartTime(time);
        else task.setEndTime(time);
        taskMapper.updateTaskStatus(task);
    }


    // 异步线程方法 创建一个新的线程来执行扫描任务
    @Async("scanExecutor")
    public void startScan(Long taskId, String target, String scanPolicy){

        try {
            log.info("开始执行任务 ID: {}, 目标: {}", taskId, target);

            // 1. 更新数据库状态为RUNNING  记录开始时间
            updateTask(taskId, "RUNNING", 10, LocalDateTime.now());

            // 2. 获取动态参数列表
            List<String> args = getArgumentsByPolicy(scanPolicy, target);

            // 3. 直接将 List 传入 ProcessBuilder 构造函数
            ProcessBuilder pb = new ProcessBuilder(args);

            // 4. (可选) 合并错误流，方便调试进度
            pb.redirectErrorStream(true);

            // 5. 启动进程
            Process process = pb.start();

            //  将流转换为字符串 (使用 Java 9+ 的 readAllBytes)
            byte[] bytes = process.getInputStream().readAllBytes();
            String xmlContent = new String(bytes, StandardCharsets.UTF_8);

            //  在控制台输出xml（为了醒目，建议加上分割线）
            log.info("======= Nmap Raw XML Output Start =======");
            System.out.println(xmlContent);
            log.info("======= Nmap Raw XML Output End =======");

            // 读取并解析 Nmap 的输出流 (XML)  使用 jackson-dataformat-xml 解析
            InputStream inputStream = new ByteArrayInputStream(bytes); // 将字符串转回流，交给解析器

            // 使用定义的xml解析器 解析 assets 和 ports 信息 并存入数据库
            nmapResultParser.parseAndSave(taskId, inputStream);

            // 4. 等待进程结束并更新状态
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                updateTask(taskId, "COMPLETED", 100, LocalDateTime.now());
            } else {
                updateTask(taskId, "FAILED", 0, LocalDateTime.now());
            }
        }
        catch (Exception e){
            log.error("扫描异常");
            updateTask(taskId, "FAILED", 0, LocalDateTime.now());
        }
    }


    // 工具方法，根据 scan_policy 返回参数列表
    public List<String> getArgumentsByPolicy(String policy, String target) {
        List<String> args = new ArrayList<>();
        args.add("nmap");

        // -sV: 探测服务版本, -T4: 加速扫描, -oX: 指定输出格式为 XML  -: 重定向到“标准输出” (stdout) 不存文件，直接吐出 给process.getInputStream() 提供数据
        switch (policy.toUpperCase()) {
            case "QUICK":
                args.addAll(Arrays.asList("-F", "-sV", "-T4", "--version-light"));
                break;
            case "DEEP":
                args.addAll(Arrays.asList("-p-", "-sV", "-O", "-T4"));
                break;
            case "STEALTH":
                args.addAll(Arrays.asList("-sS", "-Pn", "-T2"));
                break;
            default: // STANDARD 默认扫描策略
                args.addAll(Arrays.asList("--top-ports", "1000", "-sV", "-T4"));
        }

        args.add("-oX");
        args.add("-");
        args.add(target);
        return args;
    }


}
