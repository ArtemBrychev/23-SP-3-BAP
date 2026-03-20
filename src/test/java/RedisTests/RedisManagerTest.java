package RedisTests;

import io.lettuce.core.api.sync.RedisCommands;
import org.example.repositories.postgres.DataBase;
import org.example.repositories.redis.RedisManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class RedisManagerTest{
    @Container
    public static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine")
                    .withExposedPorts(6379);

    @BeforeAll
    static void setup() {
        String address = "redis://" +
                redis.getHost() + ":" +
                redis.getMappedPort(6379);

        System.setProperty("REDIS_URL", address);
    }

    @DisplayName("Корректно ли выдается commands")
    @Test
    public void getInstanceTest(){
        RedisCommands<String, String> ds1 = RedisManager.getCommands();
        assertThat(ds1).isNotNull();

        RedisCommands<String, String> ds2 = RedisManager.getCommands();
        assertThat(ds2).isNotNull();

        assertThat(ds2).isSameAs(ds1);
    }

    @DisplayName("Корректно ли выдается commands в многопоточной среде")
    @Test
    public void multiThread(){
        RedisCommands<String, String> ds1 = RedisManager.getCommands();

        ExecutorService service = Executors.newFixedThreadPool(25);

        for(int i = 0; i < 25; i++){
            service.execute(() -> {
                RedisCommands<String, String> ds = RedisManager.getCommands();
                assertThat(ds).isSameAs(ds1);
            });
        }

        service.shutdown();
    }

    @AfterAll
    static void shutdown() {
        RedisManager.shutdown();
    }
}
