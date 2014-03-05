/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.common.util;

/**
 * Action.
 * @param <T> the action to apply.
 */
public interface Action<T> {
    void apply(T param);
}
