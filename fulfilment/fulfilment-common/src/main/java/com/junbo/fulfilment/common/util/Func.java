/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.common.util;

/**
 * Func.
 *
 * @param <I> input type.
 * @param <O> output type.
 */
public interface Func<I, O> {
    O apply(I input);
}
