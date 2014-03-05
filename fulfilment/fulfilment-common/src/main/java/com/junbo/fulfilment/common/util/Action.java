/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.util;

/**
 * Action.
 *
 * @param <T> parameter type.
 */
public interface Action<T> {
    void apply(T param);
}
