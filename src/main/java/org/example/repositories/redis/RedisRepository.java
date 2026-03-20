package org.example.repositories.redis;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.ScanCursor;
import io.lettuce.core.api.sync.RedisCommands;
import org.example.repositories.Repository;
import org.example.repositories.entities.UrlEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;

public class RedisRepository implements Repository {

    private static final long TTL_TIME = 12*3600;
    private static final Logger log = LoggerFactory.getLogger(RedisRepository.class);
    private RedisCommands<String, String> cmd;

    //Сохранение ссылки в памяти {id; oldUrl}
    @Override
    public long save(UrlEntity urlEntity) {
        if(urlEntity.getId() <= 0){
            return 0;
        } else if (!check(urlEntity.getId())) {
            cmd = RedisManager.getCommands();
            cmd.setex(Long.toString(urlEntity.getId()), TTL_TIME, urlEntity.getUrlOld());
            cmd.setex(urlEntity.getUrlOld(), TTL_TIME, Long.toString(urlEntity.getId()));
            log.debug("Added {} to Redis", urlEntity);
        }

        return urlEntity.getId();
    }

    //Получение старой ссылки по новой id
    @Override
    public UrlEntity get(long id) { //Есть id, а ну мы и получаем по нему
        cmd = RedisManager.getCommands();
        if(check(id)){
            String oldUrl = cmd.get(Long.toString(id));
            UrlEntity entity = new UrlEntity();
            entity.setId(id);
            entity.setUrlOld(oldUrl);
            log.debug("Found and returning {}", entity);
            return entity;
        }else {
            return null;
        }
    }

    //Получение новой ссылки по старой oldUrl
    @Override
    public UrlEntity get(String oldUrl) { //тут идет как по старой ссылке
        if(oldUrl == null) return null;
        cmd = RedisManager.getCommands();

        String id = cmd.get(oldUrl);
        if(id!=null){
            UrlEntity entity = new UrlEntity();
            entity.setId(Long.valueOf(id));
            entity.setUrlOld(oldUrl);
            return entity;
        }
        return null;
    }

    //Проверка наличия записи по старой ссылке
    @Override
    public boolean check(String oldUrl) {
        UrlEntity entity = get(oldUrl);
        return entity != null;
    }

    //Проверка наличия по новой ссылке
    @Override
    public boolean check(long id) {
        cmd = RedisManager.getCommands();
        if(cmd.get(Long.toString(id)) != null){
            return true;
        }

        return false;
    }

    //Удаление по новой ссылке
    @Override
    public void delete(long id) {
        if(check(id)){
            cmd = RedisManager.getCommands();
            String oldUrl = cmd.get(Long.toString(id));
            cmd.del(Long.toString(id));
            cmd.del(oldUrl);
            log.debug("Removed {} from Redis storage", id);
        }
    }

    //Удаление по старой ссылке
    @Override
    public void delete(String oldUrl) {
        UrlEntity entity = get(oldUrl);
        if(entity!=null){
            cmd = RedisManager.getCommands();
            cmd.del(entity.getUrlOld());
            cmd.del(Long.toString(entity.getId()));
        }
    }
}
