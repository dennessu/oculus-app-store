/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.error;

/**
 * Interface for AppError.
 */
public interface AppError {
    /**
     * Get the http response code for this error.
     * @return The HTTP response code
     */
    int getHttpStatusCode();

    /**
     * Create an AppErrorException which contains current AppError.
     * @return The AppErrorException created.
     */
    AppErrorException exception();

    /**
     * Get the error object representing the AppError.
     * @return The error object.
     */
    Error error();
}
