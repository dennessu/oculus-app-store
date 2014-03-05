package com.goshop.catalog.common.error;

import java.util.List;

public interface AppError {
    int getHttpStatusCode();

    String getCode();

    String getMessage();

    String getField();

    List<AppError> getCauses();

    AppErrorException exception();
}
