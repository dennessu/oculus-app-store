package com.junbo.common.error;

import java.util.List;

/**
 * Created by Shenhua on 4/14/2014.
 */
public interface AppError {
    int getHttpStatusCode();

    String getCode();

    String getDescription();

    String getField();

    List<AppError> getCauses();

    AppErrorException exception();
}

