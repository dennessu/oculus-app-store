/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.filter;

import javax.annotation.Priority;
import javax.ws.rs.container.*;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SequenceIdFilter.
 */
@PreMatching
@Priority(Integer.MIN_VALUE)
public class SequenceIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String X_REQUEST_ID = "X-Request-Id";

    private final AtomicInteger sequenceId = new AtomicInteger(new Random().nextInt());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String requestId = requestContext.getHeaders().getFirst(X_REQUEST_ID);

        if (requestId == null) {
            requestId = Integer.toHexString(sequenceId.getAndIncrement());
            requestContext.getHeaders().putSingle(X_REQUEST_ID, requestId);
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        String requestId = requestContext.getHeaders().getFirst(X_REQUEST_ID);
        responseContext.getHeaders().putSingle(X_REQUEST_ID, requestId);
    }
}
