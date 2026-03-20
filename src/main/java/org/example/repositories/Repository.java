package org.example.repositories;

import org.example.exceptions.UrlNotFoundException;
import org.example.repositories.entities.UrlEntity;

public interface Repository {

    //Сохраняет ссылку (UrlEntity{id=null, oldUrl, newUrl=null})
    public long save(UrlEntity urlEntity);

    //Ну в целом... Все примерно также
    public UrlEntity get(long id);

    //Ну в целом... Все примерно также
    public UrlEntity get(String oldUrl);

    //Проверить наличие по старой ссылке
    public boolean check(String oldUrl);

    //Проверить наличие по новой
    public boolean check(long id);

    public void delete(long id);

    public void delete(String id);
}
