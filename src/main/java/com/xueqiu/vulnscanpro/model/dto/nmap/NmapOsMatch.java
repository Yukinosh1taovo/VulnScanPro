package com.xueqiu.vulnscanpro.model.dto.nmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmapOsMatch {

    @JacksonXmlProperty(isAttribute = true)
    private String name;      // 例如 "Linux 5.10"

    @JacksonXmlProperty(isAttribute = true)
    private String accuracy;  // 准确度，例如 "100"

}