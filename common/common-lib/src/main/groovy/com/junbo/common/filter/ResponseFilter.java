/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.filter;

import com.junbo.common.filter.annotations.CacheMaxAge;
import com.junbo.configuration.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Response filter.
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class ResponseFilter implements ContainerResponseFilter {

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_NAME = "common.accesscontrol.allowOrigin";
    private static final String ACCESS_CONTROL_ALLOW_HEADER_NAME = "common.accesscontrol.allowHeader";
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS_NAME = "common.accesscontrol.exposeHeaders";
    private static final String ACCESS_CONTROL_ALLOW_METHODS_NAME = "common.accesscontrol.allowMethods";
    private static final String CACHE_CONTROL_KEY = "X-Oculus-Cache-Public";

    @Autowired
    private ConfigService configService;

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();

        headers.putSingle("Access-Control-Allow-Origin", configService.getConfigValue(ACCESS_CONTROL_ALLOW_ORIGIN_NAME));
        headers.putSingle("Access-Control-Allow-Headers", configService.getConfigValue(ACCESS_CONTROL_ALLOW_HEADER_NAME));
        headers.putSingle("Access-Control-Expose-Headers", configService.getConfigValue(ACCESS_CONTROL_EXPOSE_HEADERS_NAME));
        headers.putSingle("Access-Control-Allow-Methods", configService.getConfigValue(ACCESS_CONTROL_ALLOW_METHODS_NAME));
        
        if (responseContext.getStatus() / 100 == 2) {
            for (Annotation annotation : responseContext.getEntityAnnotations()) {
                if (annotation.annotationType() == CacheMaxAge.class) {
                    CacheMaxAge cacheMaxAge = (CacheMaxAge)annotation;
                    headers.putSingle(CACHE_CONTROL_KEY, cacheMaxAge.unit().toSeconds(cacheMaxAge.duration()));
                }
            }
        }
    }
}
