/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.filter;

import com.junbo.langur.core.promise.Promise;

/**
 * The after invocation filter interface.
 * Used to process any async task after the API is about to finish.
 */
public interface AfterInvocationFilter {
    Promise<Void> process();
}
