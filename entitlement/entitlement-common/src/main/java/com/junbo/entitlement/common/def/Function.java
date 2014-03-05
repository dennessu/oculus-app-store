/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.common.def;

/**
 * A common function interface.
 *
 * @param <T> returned type
 * @param <U> action type
 */
public interface Function<T, U> {
    T apply(U u);
}
