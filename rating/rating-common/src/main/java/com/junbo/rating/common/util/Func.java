/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.common.util;

/**
 * Created by lizwu on 2/19/14.
 * @param <I> input type
 * @param <O> return type
 */
public interface Func<I, O> {
    O execute(I param);
}
