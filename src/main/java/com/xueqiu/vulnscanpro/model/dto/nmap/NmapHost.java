package com.xueqiu.vulnscanpro.model.dto.nmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmapHost {

    @JacksonXmlProperty(localName = "address")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NmapAddress> addresses;

    @JacksonXmlProperty(localName = "hostnames")
    private NmapHostnames hostnames;

    @JacksonXmlProperty(localName = "ports")
    private NmapPorts ports;

    @JacksonXmlProperty(localName = "os")
    private NmapOs os; // 新增：处理 -O 参数产生的信息

}