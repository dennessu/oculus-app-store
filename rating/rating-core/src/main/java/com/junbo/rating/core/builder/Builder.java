/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.builder;

/**
 * Created by lizwu on 7/8/14.
 */
public interface Builder<T> {
    void fromRequest(T request);
    T buildResult();
}
