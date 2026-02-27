package org.marensovich.Utils.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader {
    private static ConfigData config;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("config.yml")) {
            if (inputStream == null) {
                throw new RuntimeException("Файл config.yml не найден!");
            }
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            config = mapper.readValue(inputStream, ConfigData.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки config.yml", e);
        }
    }

    public static ConfigData getConfig() {
        return config;
    }
}
