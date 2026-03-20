package org.example.exceptions;

public class UrlNotFoundException extends BaseException {

    public UrlNotFoundException(String url) {
        super(ExceptionType.URL_NOT_FOUND);
    }
}
