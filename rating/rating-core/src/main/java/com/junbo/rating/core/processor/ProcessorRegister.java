/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import org.springframework.util.Assert;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Subs Rating Processor Register.
 */
public final class ProcessorRegister {
    private static ConcurrentHashMap<String, Processor> processorRegister;

    public void setProcessorRegister(ConcurrentHashMap<String, Processor> processorRegister) {
        this.processorRegister = processorRegister;
    }

    public static Processor getProcessor(String subsRatingType) {
        Assert.notNull(subsRatingType, "subsRatingType");

        Processor processor = processorRegister.get(subsRatingType);
        if (processor == null) {
            throw new IllegalStateException(subsRatingType + " processor is not registered.");
        }

        return processor;
    }
}
