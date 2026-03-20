package CacheTests;

import org.example.repositories.CachedRepository;
import org.example.repositories.entities.UrlEntity;
import org.example.repositories.postgres.DbRepository;
import org.example.repositories.redis.RedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CacheRepositoryTest {

    DbRepository dbRepository;
    RedisRepository redisRepository;
    CachedRepository cachedRepository;

    AtomicBoolean addedToDb;
    AtomicBoolean addedToRedis;

    AtomicReference<UrlEntity> dbStorage;
    AtomicReference<UrlEntity> redisStorage;

    @BeforeEach
    public void init() {

        dbRepository = mock(DbRepository.class);
        redisRepository = mock(RedisRepository.class);

        addedToDb = new AtomicBoolean(false);
        addedToRedis = new AtomicBoolean(false);

        dbStorage = new AtomicReference<>();
        redisStorage = new AtomicReference<>();

        when(dbRepository.save(any(UrlEntity.class))).thenAnswer(ans -> {
            UrlEntity entity = ans.getArgument(0);
            entity.setId(1L);
            addedToDb.set(true);
            dbStorage.set(new UrlEntity(entity));
            return 1L;
        });

        when(dbRepository.check(anyString()))
                .thenAnswer(ans -> addedToDb.get());

        when(dbRepository.check(anyLong()))
                .thenAnswer(ans -> addedToDb.get());

        when(dbRepository.get(anyLong()))
                .thenAnswer(ans -> dbStorage.get());

        when(dbRepository.get(anyString()))
                .thenAnswer(ans -> dbStorage.get());

        doAnswer(ans -> {
            addedToDb.set(false);
            dbStorage.set(null);
            return null;
        }).when(dbRepository).delete(anyLong());

        when(redisRepository.save(any(UrlEntity.class))).thenAnswer(ans -> {
            UrlEntity entity = ans.getArgument(0);
            addedToRedis.set(true);
            redisStorage.set(new UrlEntity(entity));
            return entity.getId();
        });

        when(redisRepository.check(anyString()))
                .thenAnswer(ans -> addedToRedis.get());

        when(redisRepository.check(anyLong()))
                .thenAnswer(ans -> addedToRedis.get());

        when(redisRepository.get(anyLong()))
                .thenAnswer(ans -> redisStorage.get());

        when(redisRepository.get(anyString()))
                .thenAnswer(ans -> redisStorage.get());

        doAnswer(ans -> {
            addedToRedis.set(false);
            redisStorage.set(null);
            return null;
        }).when(redisRepository).delete(anyLong());

        cachedRepository = new CachedRepository(dbRepository, redisRepository);
    }

    @DisplayName("Добавление новой ссылки")
    @Test
    public void addNewUrlTest() {

        UrlEntity entity = new UrlEntity("OldUrl1");

        long id = cachedRepository.save(entity);

        assertThat(id).isEqualTo(1L);
        assertThat(addedToDb.get()).isTrue();
        assertThat(addedToRedis.get()).isTrue();

        verify(redisRepository, times(1)).check("OldUrl1");
        verify(dbRepository, times(1)).check("OldUrl1");

        verify(dbRepository, times(1)).save(any(UrlEntity.class));
        verify(redisRepository, times(1)).save(any(UrlEntity.class));
    }

    @DisplayName("Получение новой ссылки (hit в Redis)")
    @Test
    public void getNewUrlTest() {

        UrlEntity entity = new UrlEntity("OldUrl1");
        long id = cachedRepository.save(entity);

        UrlEntity result = cachedRepository.get(id);

        verify(redisRepository, times(1)).check(id);
        verify(redisRepository, times(1)).get(id);

        verify(dbRepository, never()).get(id);

        assertThat(result).isEqualTo(redisStorage.get());
    }

    @DisplayName("Получение по id: нет в Redis, есть в DB")
    @Test
    public void getFromDbWhenRedisMissTest() {

        UrlEntity entity = new UrlEntity("OldUrl1");
        long id = cachedRepository.save(entity);

        addedToRedis.set(false);
        redisStorage.set(null);

        UrlEntity result = cachedRepository.get(id);

        verify(redisRepository, times(1)).check(id);
        verify(dbRepository, times(1)).check(id);

        verify(dbRepository, times(1)).get(id);
        verify(redisRepository, times(2)).save(any(UrlEntity.class));

        assertThat(result).isEqualTo(dbStorage.get());
        assertThat(addedToRedis.get()).isTrue();
    }

    @DisplayName("Получение по id: нет нигде")
    @Test
    public void getNotFoundTest() {

        UrlEntity result = cachedRepository.get(1L);

        verify(redisRepository, times(1)).check(1L);
        verify(dbRepository, times(1)).check(1L);

        verify(redisRepository, never()).get(anyLong());
        verify(dbRepository, never()).get(anyLong());

        assertThat(result).isNull();
    }
}