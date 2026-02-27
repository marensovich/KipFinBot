package org.marensovich.Utils.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {
    public String logLevel;
    public int timeout;
    public List<String> adminLinks;
    public String link_info_support;
}
