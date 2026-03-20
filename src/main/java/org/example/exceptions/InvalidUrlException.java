package org.example.exceptions;

public class InvalidUrlException extends BaseException{
    public InvalidUrlException(String str){
        super(ExceptionType.INVALID_URL_EXCEPTION);
    }

}
