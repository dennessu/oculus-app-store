/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.rating.core.context.SubsRatingContext;
import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Subs Rating Processor Register.
 */
public final class ProcessorRegister {
    private static ConcurrentHashMap<String, Processor> processorRegister;

    public static void setProcessorRegister(ConcurrentHashMap<String, Processor> processorRegister) {
        ProcessorRegister.processorRegister = processorRegister;
    }

    public static void doProcess(SubsRatingContext context) {
        Assert.notNull(context, "subsRatingContext");
        Assert.notNull(context.getType(), "subsRatingType");

        Processor processor = processorRegister.get(context.getType().toString());
        if (processor == null) {
            throw new IllegalStateException(context.getType() + " processor is not registered.");
        }

        processor.process(context);
    }
}
