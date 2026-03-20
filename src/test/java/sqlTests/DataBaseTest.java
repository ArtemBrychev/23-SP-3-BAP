package sqlTests;

import org.example.repositories.postgres.DataBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class DataBaseTest {

    @Container
    static PostgreSQLContainer<?> postgres
            = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdatabase")
            .withUsername("testadmin")
            .withPassword("testpassword");

    @BeforeEach
    public void init() {
        System.setProperty("DB_URL", postgres.getJdbcUrl());
        System.setProperty("DB_USER", postgres.getUsername());
        System.setProperty("DB_PASSWORD", postgres.getPassword());

        System.out.println("Postgres container JDBC URL: " + postgres.getJdbcUrl());
        System.out.println("System property URL: " + System.getProperty("DB_URL"));
    }

    @DisplayName("Корректно ли выдается DataSource")
    @Test
    public void getInstanceTest(){
        DataSource ds1 = DataBase.getInstance();
        assertThat(ds1).isNotNull();

        DataSource ds2 = DataBase.getInstance();
        assertThat(ds2).isNotNull();

        assertThat(ds2).isSameAs(ds1);
    }

    @DisplayName("Корректно ли выдается DataSource в многопоточной среде")
    @Test
    public void multiThread(){
        DataSource ds1 = DataBase.getInstance();

        ExecutorService service = Executors.newFixedThreadPool(25);

        for(int i = 0; i < 25; i++){
            service.execute(() -> {
                DataSource ds = DataBase.getInstance();
                assertThat(ds).isSameAs(ds1);
            });
        }

        service.shutdown();
    }
}
