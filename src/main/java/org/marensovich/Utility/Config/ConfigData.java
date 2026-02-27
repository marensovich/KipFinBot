package org.marensovich.Utils.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigData {
    public Bot bot;
    public Messages messages;
    public Settings settings;
}

