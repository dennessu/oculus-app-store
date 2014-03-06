/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.common;

/**
 * filter condition interface.
 * @param <T> element type
 */
public interface IPredicate<T> {
    boolean apply(T type);
}

