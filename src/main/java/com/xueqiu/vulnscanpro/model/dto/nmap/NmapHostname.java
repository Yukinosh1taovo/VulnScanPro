package com.xueqiu.vulnscanpro.model.dto.nmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmapHostname {
    @JacksonXmlProperty(isAttribute = true)
    private String name; // 域名/主机名字符串

    @JacksonXmlProperty(isAttribute = true)
    private String type; // 类型，如 "user" 或 "PTR"
}