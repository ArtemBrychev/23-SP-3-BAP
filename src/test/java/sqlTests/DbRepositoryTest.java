package sqlTests;

import org.example.repositories.entities.UrlEntity;
import org.example.repositories.postgres.DbRepository;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
public class DbRepositoryTest {
    DbRepository repository;
    UrlEntity entity;

    @Container
    static PostgreSQLContainer<?> postgres
            = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdatabase")
            .withUsername("testadmin")
            .withPassword("testpassword");

    @BeforeEach
    public void init(){
        System.setProperty("DB_URL", postgres.getJdbcUrl());
        System.setProperty("DB_USER", postgres.getUsername());
        System.setProperty("DB_PASSWORD", postgres.getPassword());

        System.out.println("Postgres container JDBC URL: " + postgres.getJdbcUrl());


        repository = new DbRepository();
        entity = new UrlEntity();
        entity.setUrlOld("url.com");
        //entity.setUrlNew("newurl.com");
        entity.setId(repository.save(entity));
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
}
