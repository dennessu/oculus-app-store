/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.client;

import com.ning.http.client.Response;

/**
 * ResponseHeadersHandler.
 */
public interface ResponseHandler {
    /**
     * Method to provide headers which will get passed through when routing to another endpoint.
     */
    void onResponse(Response response);
}
