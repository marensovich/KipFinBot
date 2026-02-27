package org.marensovich.Utils.Config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Bot {
    public String token;
    public String username;
}
