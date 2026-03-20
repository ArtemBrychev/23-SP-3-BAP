package org.example.exceptions;

import org.example.repositories.Repository;

public enum ExceptionType {

    URL_NOT_FOUND("Данная ссылка не найдена"),
    REPOSITORY_EXCEPTION("Проблема при обращении к данным"),
    INVALID_URL_EXCEPTION("Ошибка в url ресурса");


    private String message;

    private ExceptionType(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }

}
