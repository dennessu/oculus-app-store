/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.provider;

import java.util.Map;

/**
 * Interface of Response.
 */
public interface Response {
    String getBody();
    void setBody(String body);
    Map<String, String> getHeaders();
    void setHeaders(Map<String, String> headers);
    Integer getStatusCode();
    void setStatusCode(Integer statusCode);
    String getMessage();
    void setMessage(String message);
}
