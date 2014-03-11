/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.core.provider;

import java.util.Map;

/**
 * Interface of Request.
 */
public interface Request {
    Map<String, String> getQueries();
    void setQueries(Map<String, String> queries);
    String getUri();
    void setUri(String uri);
    Map<String, String> getHeaders();
    void setHeaders(Map<String, String> headers);
    String getJson();
    void setJson(String json);
}
