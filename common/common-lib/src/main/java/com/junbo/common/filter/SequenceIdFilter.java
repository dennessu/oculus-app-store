/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.filter;

import org.slf4j.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SequenceIdFilter.
 */
public class SequenceIdFilter implements ContainerRequestFilter {
    private static final String SEQID = "SEQID";
    private AtomicInteger sequenceId = new AtomicInteger(new Random().nextInt());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        MDC.put(SEQID, Integer.toString(sequenceId.getAndIncrement()));
    }
}
