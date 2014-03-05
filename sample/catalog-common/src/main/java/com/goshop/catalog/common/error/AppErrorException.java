package com.goshop.catalog.common.error;

public class AppErrorException extends RuntimeException {
    private final AppError error;

    public AppError getError() {
        return error;
    }

    public AppErrorException(AppError error) {
        super(error.getMessage());
        this.error = error;
    }

    public AppErrorException(AppError error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }
}
