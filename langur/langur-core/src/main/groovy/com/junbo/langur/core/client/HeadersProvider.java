/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.client;

import javax.ws.rs.core.MultivaluedMap;

/**
 * HeadersProvider.
 */
public interface HeadersProvider {
    /**
     * Method to provide headers which will get passed through when routing to another endpoint.
     * @return the headers to be passed through to another endpoint.
     */
    MultivaluedMap<String, String> getHeaders();
}
