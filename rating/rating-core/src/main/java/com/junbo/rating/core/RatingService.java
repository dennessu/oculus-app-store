/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core;

import com.junbo.rating.core.context.RatingContext;

/**
 * Created by lizwu on 2/7/14.
 */
public interface RatingService<T extends RatingContext> {
    void rate(T context);
}
