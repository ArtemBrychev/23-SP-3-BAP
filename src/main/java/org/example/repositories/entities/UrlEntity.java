package org.example.repositories.entities;

import java.util.Objects;

public class UrlEntity {

    private long id;
    private String urlOld;
    private String urlNew;

    public UrlEntity(){}

    public UrlEntity(String urlOld){
        this.urlOld = urlOld;
    }

    public UrlEntity(long id, String urlOld){
        this.id = id;
        this.urlOld = urlOld;
    }

    public UrlEntity(UrlEntity entity){
        this.id = entity.getId();
        this.urlNew = entity.getUrlNew();
        this.urlOld = entity.getUrlOld();
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public void setUrlOld(String urlOld){
        this.urlOld = urlOld;
    }

    public String getUrlOld(){
        return urlOld;
    }

    public void setUrlNew(String urlNew){
        this.urlNew = urlNew;
    }

    public String getUrlNew(){
        return urlNew;
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof UrlEntity urlEntity){
            if(id==0) {
                return this.urlOld.equals(urlEntity.getUrlOld());
            }else{
                return id== urlEntity.getId();
            }
        }else{
            return false;
        }
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, urlOld);
    }

    @Override
    public String toString(){
        return String.format("id={%s} oldUrl={%s} bewUrl={%s}",
                    id,
                    urlOld,
                    urlNew
                );
    }
}
