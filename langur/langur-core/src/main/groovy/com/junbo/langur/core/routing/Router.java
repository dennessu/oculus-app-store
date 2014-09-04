/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.routing;

/**
 * The router interface.
 */
public interface Router {
    /**
     * Method to determine the routing destination of the API request.
     * The logic to check whether the request has hops is expected within the method using contextual headers.
     * If maxhops are exceeded, throw exception to terminate the routing.
     * @return The target URL for the request to be forwarded. Returns null if the request can be handled locally.
     */
    String getTargetUrl(Class<?> resourceClass, Object[] routingParams);

    /**
     * Method to determine the data access policy to the API request in case of in-proc calls.
     */
    void resolveDataAccessPolicy(Class<?> resourceClass);
}
