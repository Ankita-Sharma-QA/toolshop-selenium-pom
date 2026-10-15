package com.toolshop.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads values from config.properties.
 *
 * A value passed on the command line wins over the file. For example:
 *   mvn test -Dbrowser=firefox -Dheadless=true
 * This is how the CI pipeline runs the same tests in different browsers.
 */
public class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in src/main/resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not read config.properties", e);
        }
    }

    public static String get(String key) {
        // 1. Check command line (-Dkey=value)
        String value = System.getProperty(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }
        // 2. Fall back to config.properties
        value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key '" + key + "' is missing in config.properties");
        }
        return value;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
