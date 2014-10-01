/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.core.track;

/**
 * Used to log the user action after changing the entities.
 */
public interface UserLogProcessor {
    void process(Object result);
}
