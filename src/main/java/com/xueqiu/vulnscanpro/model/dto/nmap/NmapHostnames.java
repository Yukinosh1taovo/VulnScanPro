package com.xueqiu.vulnscanpro.model.dto.nmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;
import java.util.List;



@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NmapHostnames {
    @JacksonXmlProperty(localName = "hostname")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<NmapHostname> hostnameList;
}