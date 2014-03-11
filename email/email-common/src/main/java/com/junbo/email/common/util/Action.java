/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.common.util;

/**
 * Interface of Action.
 * @param <T>
 */
public interface Action<T> {
    void apply(T param);
}
