package com.xueqiu.vulnscanpro.model.dto.nmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmapAddress {
    @JacksonXmlProperty(isAttribute = true)
    private String addr;
    @JacksonXmlProperty(isAttribute = true)
    private String addrtype; // ipv4 或 mac
}