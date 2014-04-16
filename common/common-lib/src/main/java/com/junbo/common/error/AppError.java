/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

import java.util.List;

/**
 * Interface for AppError.
 */
public interface AppError {
    int getHttpStatusCode();

    String getCode();

    String getDescription();

    String getField();

    List<AppError> getCauses();

    AppErrorException exception();

    Error error();
}
