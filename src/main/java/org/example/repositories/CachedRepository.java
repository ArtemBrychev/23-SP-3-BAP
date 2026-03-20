package org.example.repositories;

import org.example.repositories.entities.UrlEntity;
import org.example.repositories.postgres.DbRepository;
import org.example.repositories.redis.RedisRepository;

public class CachedRepository implements Repository{

    private final Repository dbRepository;
    private final Repository redisRepository;

    public CachedRepository(Repository dbRepository,
                            Repository redisRepository) {
        this.dbRepository = dbRepository;
        this.redisRepository = redisRepository;
    }


    @Override
    public long save(UrlEntity urlEntity) {
        if(redisRepository.check(urlEntity.getUrlOld())){
            return redisRepository.get(urlEntity.getUrlOld()).getId();
        }
        if(dbRepository.check(urlEntity.getUrlOld())){
            return dbRepository.get(urlEntity.getUrlOld()).getId();
        }

        long id = dbRepository.save(urlEntity);
        urlEntity.setId(id);
        redisRepository.save(urlEntity);
        return id;
    }

    @Override
    public UrlEntity get(long id) {
        if(redisRepository.check(id)){
             return redisRepository.get(id);
        }else if(dbRepository.check(id)){
            UrlEntity entity = dbRepository.get(id);
            redisRepository.save(entity);
            return entity;
        }

        return null;
    }

    @Override
    public UrlEntity get(String oldUrl) {
        if(redisRepository.check(oldUrl)){
            return redisRepository.get(oldUrl);
        }else if(dbRepository.check(oldUrl)){
            UrlEntity entity = dbRepository.get(oldUrl);
            redisRepository.save(entity);
            return entity;
        }

        return null;
    }

    @Override
    public boolean check(String oldUrl) {
        if(redisRepository.check(oldUrl)){
            return true;
        }
        return dbRepository.check(oldUrl);
    }

    @Override
    public boolean check(long id) {
        if(redisRepository.check(id)){
            return true;
        }
        return dbRepository.check(id);
    }

    @Override
    public void delete(long id) {
        redisRepository.delete(id);
        dbRepository.delete(id);
    }

    @Override
    public void delete(String oldUrl) {
        redisRepository.delete(oldUrl);
        dbRepository.delete(oldUrl);
    }
}
