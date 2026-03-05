package com.xueqiu.vulnscanpro.model.dto.nmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmapState {
    @JacksonXmlProperty(isAttribute = true)
    private String state; // open, closed
}