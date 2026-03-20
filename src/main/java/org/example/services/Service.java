package org.example.services;

import org.example.exceptions.InvalidUrlException;
import org.example.exceptions.UrlNotFoundException;
import org.example.repositories.Repository;
import org.example.repositories.entities.UrlEntity;
import org.example.repositories.postgres.DbRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Service {
    private final Repository repository;
    private static final Logger log = LoggerFactory.getLogger(Service.class);

    public Service(Repository repository){
        this.repository = repository;
    }

    public String add(String url){
        if(!UrlValidator.isValidExternalUrl(url)){
            throw new InvalidUrlException("Url не прошла проверку на валидность");
        }
        log.info("Добавляем новую строку{}", url);
        UrlEntity entity = new UrlEntity();
        entity.setUrlOld(url);

        long newId = repository.save(entity);
        String encodedUrl = Base62Codec.encode(newId);
        log.info("Service.add: {} -> {}({})", url, encodedUrl, newId);
        return encodedUrl;
    }

    public String get(String url){
        long id = Base62Codec.decode(url);

        if(repository.check(id)){
            UrlEntity entity = repository.get(id);
            log.info("{} -> {}({})", url, entity.getUrlOld(), id);
            return entity.getUrlOld();
        }else{
            log.info("Url:{} is not found", url);
            throw new UrlNotFoundException("Url не найдена");
        }
    }
}
