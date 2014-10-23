/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.junbo.common.filter.annotations.CacheMaxAge;
import com.junbo.configuration.ConfigServiceManager;
import com.junbo.langur.core.profiling.ProfilingHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Singleton;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Response filter.
 */
@Provider
@Singleton
@Priority(Priorities.HEADER_DECORATOR)
public class ResponseFilter implements ContainerResponseFilter {
    private static Logger profilingLogger = LoggerFactory.getLogger(ProfilingHelper.class);

    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_NAME = "common.accesscontrol.allowOrigin";
    private static final String ACCESS_CONTROL_ALLOW_HEADER_NAME = "common.accesscontrol.allowHeader";
    private static final String ACCESS_CONTROL_EXPOSE_HEADERS_NAME = "common.accesscontrol.exposeHeaders";
    private static final String ACCESS_CONTROL_ALLOW_METHODS_NAME = "common.accesscontrol.allowMethods";

    private static final String CACHE_CONTROL_KEY = "X-Oculus-Cache-Public";
    private static final String PROFILE_OUTPUT_KEY = ProfilingHelper.PROFILE_OUTPUT_KEY;

    private final String allowOrigin;
    private final String allowHeader;
    private final String exposeHeaders;
    private final String allowMethods;

    public ResponseFilter() {
        allowOrigin = ConfigServiceManager.instance().getConfigValue(ACCESS_CONTROL_ALLOW_ORIGIN_NAME);
        allowHeader = ConfigServiceManager.instance().getConfigValue(ACCESS_CONTROL_ALLOW_HEADER_NAME);
        exposeHeaders = ConfigServiceManager.instance().getConfigValue(ACCESS_CONTROL_EXPOSE_HEADERS_NAME);
        allowMethods = ConfigServiceManager.instance().getConfigValue(ACCESS_CONTROL_ALLOW_METHODS_NAME);
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();

        if (StringUtils.isNotBlank(allowOrigin)) {
            headers.putSingle("Access-Control-Allow-Origin", allowOrigin);
        }

        if (StringUtils.isNotBlank(allowHeader)) {
            headers.putSingle("Access-Control-Allow-Headers", allowHeader);
        }

        if (StringUtils.isNotBlank(exposeHeaders)) {
            headers.putSingle("Access-Control-Expose-Headers", exposeHeaders);
        }

        if (StringUtils.isNotBlank(allowMethods)) {
            headers.putSingle("Access-Control-Allow-Methods", allowMethods);
        }

        if (responseContext.getStatus() / 100 == 2) {
            for (Annotation annotation : responseContext.getEntityAnnotations()) {
                if (annotation.annotationType() == CacheMaxAge.class) {
                    CacheMaxAge cacheMaxAge = (CacheMaxAge) annotation;
                    headers.putSingle(CACHE_CONTROL_KEY, cacheMaxAge.unit().toSeconds(cacheMaxAge.duration()));
                    break;
                }
            }
        }

        if (ProfilingHelper.hasProfileOutput()) {
            String data = ProfilingHelper.dumpProfileData();

            if (profilingLogger.isDebugEnabled()) {
                profilingLogger.debug(ProfilingHelper.prettyPrint(data));
            }

            // keep header line small and make it smaller than 64k
            // Netty supports headers up to 64k.
            Iterable<String> chunks = Splitter.fixedLength(8000).split(data);
            List<String> chunkList = Lists.newArrayList(chunks);
            int maxHeaders = 7;
            int toSize = Math.min(chunkList.size(), maxHeaders);
            for (int i = 0; i < toSize; ++i) {
                headers.add(PROFILE_OUTPUT_KEY, chunkList.get(i));
            }
            if (chunkList.size() > maxHeaders) {
                headers.add(PROFILE_OUTPUT_KEY, "... (truncated)");
            }

            ProfilingHelper.clear();
        }
    }
}
