/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.client;

import com.ning.http.client.Response;

/**
 * ExceptionHandler.
 */
public interface ExceptionHandler {
    /**
     * Method to handle the error response, please make sure you throw exception after parsing the response.
     * @param response
     */
    void handleExceptionResponse(Response response);
}
