package org.example.repositories.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import org.example.configs.EnvConfig;
import org.example.repositories.postgres.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RedisManager {
    private static RedisClient redisClient;
    private static StatefulRedisConnection<String, String> connection;
    private static RedisCommands<String, String> commands;
    private static final Logger log = LoggerFactory.getLogger(RedisManager.class);


    public static RedisCommands<String, String> getCommands() {
        if(commands==null){
            synchronized (RedisManager.class){
                if(commands==null){
                    log.debug("In RedisManager initialization block");
                    String redisUrl = EnvConfig.get("REDIS_URL");
                    log.info("Redis url={}", redisUrl);

                    redisClient = RedisClient.create(redisUrl);
                    connection = redisClient.connect();
                    commands = connection.sync();
                }
            }
        }

        return commands;
    }

    public static void shutdown() {
        if (connection != null) {
            connection.close();
        }
        if (redisClient != null) {
            redisClient.shutdown();
        }
        commands = null;
    }
}
