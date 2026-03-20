package org.example.exceptions;

public abstract class BaseException extends RuntimeException {

    private final ExceptionType type;

    protected BaseException(ExceptionType type) {
        super(type.getMessage());
        this.type = type;
    }

    protected BaseException(ExceptionType type, Throwable cause) {
        super(type.getMessage(), cause);
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }
}
