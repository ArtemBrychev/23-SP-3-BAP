package RedisTests;

import org.example.repositories.Repository;
import org.example.repositories.entities.UrlEntity;
import org.example.repositories.postgres.DbRepository;
import org.example.repositories.redis.RedisManager;
import org.example.repositories.redis.RedisRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class RedisRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(RedisRepositoryTest.class);
    Repository repository;
    UrlEntity entity;

    @Container
    public static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine")
                    .withExposedPorts(6379);

    @BeforeAll
    static void setup() {
        String address = "redis://" +
                redis.getHost() + ":" +
                redis.getMappedPort(6379);

        log.debug("setup method, redis url:{}", address);

        System.setProperty("REDIS_URL", address);
    }

    @BeforeEach
    public void init(){
        repository = new RedisRepository();
        entity = new UrlEntity();
        entity.setUrlOld("url.com");
        entity.setId(1);
        repository.save(entity);
    }


    @AfterEach
    public void close(){
        repository.delete(entity.getUrlOld());
    }

    @DisplayName("Проверка наличия ссылки по старой ссылке")
    @Test
    public void checkUrlTest(){
        assertThat(repository.check(entity.getUrlOld())).isTrue();
    }

    @DisplayName("Проверка наличия ссылки по id")
    @Test
    public void checkIdTest(){
        assertThat(repository.check(entity.getId())).isTrue();
    }

    @DisplayName("Проверка наличия ссылки, которой нет ")
    @Test
    public void FalseChechkUrlTest(){
        assertThat(repository.check("randomUrl.com")).isFalse();
    }

    @DisplayName("Проверка наличия id, которого нет ")
    @Test
    public void FalseChechkIdTest(){
        assertThat(repository.check(45457385L)).isFalse();
    }

    @DisplayName("Проверка получения новой ссылки")
    @Test
    public void getTest(){
        UrlEntity result = repository.get(entity.getId());
        assertThat(result).isNotNull();
        assertThat(result.getUrlNew()).isEqualTo(entity.getUrlNew());
    }

    @DisplayName("Проверка получения несуществующей ссылки")
    @Test
    public void getUnexistentTest(){
        UrlEntity result = repository.get(2325235235L);
        assertThat(result).isNull();
    }

    @DisplayName("Попытка добавления уже существующей ссылки")
    @Test
    public void saveExistentTest(){
        UrlEntity newEntity = new UrlEntity(entity);

        newEntity.setId(repository.save(newEntity));
        assertThat(repository.check(entity.getUrlOld())).isTrue();

        UrlEntity result = repository.get(newEntity.getId());
        assertThat(result).isEqualTo(entity);
        assertThat(result).isEqualTo(newEntity);

    }

    @DisplayName("Удаление ссылки")
    @Test
    public void deleteTest(){
        assertThat(repository.check(entity.getUrlOld())).isTrue();
        repository.delete(entity.getUrlOld());
        assertThat(repository.check(entity.getUrlOld())).isFalse();
        assertThat(repository.get(entity.getId())).isNull();

    }

    @AfterAll
    static void shutdown() {
        RedisManager.shutdown();
    }

}
