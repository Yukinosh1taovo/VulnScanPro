package com.xueqiu.vulnscanpro.engine;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.xueqiu.vulnscanpro.mapper.AssetMapper;
import com.xueqiu.vulnscanpro.mapper.PortMapper;
import com.xueqiu.vulnscanpro.model.entity.Asset;
import com.xueqiu.vulnscanpro.model.entity.Port;
import com.xueqiu.vulnscanpro.model.dto.nmap.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class NmapResultParser {  // 解析nmap输出xml信息类

    private final AssetMapper assetMapper;
    private final PortMapper portMapper;
    private final XmlMapper xmlMapper = new XmlMapper(); // Jackson XML 映射器


    // 解析xml结果并存入数据库
    @Transactional // 事务保证一个任务的资产和端口同时入库
    public void parseAndSave(Long taskId, InputStream xmlStream) {
        try {
            // 1. 将 XML 反序列化为 Java 对象
            NmapResult result = xmlMapper.readValue(xmlStream, NmapResult.class);

            if (result.getHosts() == null || result.getHosts().isEmpty()) {
                log.warn("任务 {} 未发现任何存活主机或开放端口", taskId);
                return;
            }

            for (NmapHost host : result.getHosts()) {

                // 2. 提取 IP 地址
                String ip = host.getAddresses().stream()
                        .filter(a -> "ipv4".equals(a.getAddrtype()))
                        .map(NmapAddress::getAddr)
                        .findFirst().orElse("Unknown");

                // 3. 提取并处理 Hostname (公网存域名，内网存主机名)
                String rawHostname = extractRawHostname(host);
                if (rawHostname != null) {
                    // 内网对象：如果是类似 pc1.local，截取 pc1；如果没有点，直接存
                    if (isInternalIp(ip)) {
                        rawHostname = rawHostname.contains(".") ? rawHostname.split("\\.")[0] : rawHostname;
                    }
                    // 公网对象：存完整域名
                }


                // 4. 提取 OS Guess (从服务信息中启发式获取)
                String osGuess = extractOsGuess(host);


                // 3. 构造并保存 Asset 对象
                Asset asset = new Asset();
                asset.setTaskId(taskId);
                asset.setIpAddress(ip);
                asset.setHostname(rawHostname);
                asset.setOsGuess(osGuess);
                asset.setLastSeen(LocalDateTime.now());

                assetMapper.insert(asset); // 这里利用 useGeneratedKeys 获取 id

                // 4. 解析该 Asset 下的所有端口
                savePorts(asset.getId(), host);
            }
        } catch (Exception e) {
            log.error("解析 Nmap XML 失败", e);
        }
    }


    /**
     * 从 Hostnodes 中提取原始名称
     */
    private String extractRawHostname(NmapHost host) {
        if (host.getHostnames() != null && host.getHostnames().getHostnameList() != null
                && !host.getHostnames().getHostnameList().isEmpty()) {
            return host.getHostnames().getHostnameList().get(0).getName();
        }
        return null;
    }



    /**
     * 启发式提取操作系统信息
     */
    private String extractOsGuess(NmapHost host) {

        // 优先级 1：提取 -O 参数带来的精确操作系统探测结果
        if (host.getOs() != null && host.getOs().getOsMatches() != null && !host.getOs().getOsMatches().isEmpty()){
            // 取准确度最高的第一个匹配项
            NmapOsMatch bestMatch = host.getOs().getOsMatches().get(0);
            return bestMatch.getName() + " (" + bestMatch.getAccuracy() + "%)";
        }


        // 优先级 2：从服务探测 (-sV) 的 ostype 属性中提取（之前的逻辑）
        if (host.getPorts() != null && host.getPorts().getPortList() != null) {
            String serviceOs = host.getPorts().getPortList().stream()
                    .filter(p -> p.getService() != null && p.getService().getOsType() != null)
                    .map(p -> p.getService().getOsType())
                    .findFirst()
                    .orElse(null);
            if (serviceOs != null) return serviceOs;
        }

        return "Unknown";
    }


    /**
     * 判断是否为内网 IP (RFC 1918)
     */
    private boolean isInternalIp(String ip) {
        if ("127.0.0.1".equals(ip) || "localhost".equalsIgnoreCase(ip)) return true;
        // 简单正则匹配 10.x, 192.168.x, 172.16-31.x
        return ip.startsWith("10.") ||
                ip.startsWith("192.168.") ||
                (ip.startsWith("172.") && isPrivateSixteenToThirtyOne(ip));
    }


    private boolean isPrivateSixteenToThirtyOne(String ip) {
        try {
            int secondOctet = Integer.parseInt(ip.split("\\.")[1]);
            return secondOctet >= 16 && secondOctet <= 31;
        } catch (Exception e) {
            return false;
        }
    }


    //  解析对应 Asset 下的所有端口
    private void savePorts(Long assetId, NmapHost host) {
        if (host.getPorts() == null || host.getPorts().getPortList() == null) return;
        for (NmapPortInfo pInfo : host.getPorts().getPortList()) {
            Port port = new Port();
            port.setAssetId(assetId);
            port.setPortNumber(pInfo.getPortId());
            port.setProtocol(pInfo.getProtocol());
            port.setState(pInfo.getState() != null ? pInfo.getState().getState() : "unknown");

            if (pInfo.getService() != null) {
                port.setServiceName(pInfo.getService().getName());
                String version = (pInfo.getService().getProduct() != null ? pInfo.getService().getProduct() : "")
                        + " " + (pInfo.getService().getVersion() != null ? pInfo.getService().getVersion() : "");
                port.setVersionInfo(version.trim());
            }
            portMapper.insert(port);
        }
    }



}