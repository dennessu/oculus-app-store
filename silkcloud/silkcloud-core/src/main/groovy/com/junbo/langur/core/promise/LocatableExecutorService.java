// CHECKSTYLE:OFF

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.promise;

import java.util.concurrent.ExecutorService;

/**
 * Scoped executor service.
 */
public interface LocatableExecutorService extends ExecutorService {
    /**
     * Determine whether current thread is in specified executor.
     * @return whether current thread is in specified executor
     */
    boolean isExecutorThread();
}
