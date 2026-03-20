package org.example.configs;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private EnvConfig() {}

    public static String get(String key) {
        String value = System.getenv(key);
        if (value != null) return value;
        value = System.getProperty(key);
        if(value!=null) return value;
        return dotenv.get(key);
    }
}
