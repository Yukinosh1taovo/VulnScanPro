package com.xueqiu.vulnscanpro.model.dto.nmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmapPortInfo {
    @JacksonXmlProperty(isAttribute = true)
    private String protocol;
    @JacksonXmlProperty(isAttribute = true, localName = "portid")
    private Integer portId;
    @JacksonXmlProperty(localName = "state")
    private NmapState state;
    @JacksonXmlProperty(localName = "service")
    private NmapService service;
}