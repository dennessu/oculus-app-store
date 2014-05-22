/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.rating.core.context.SubsRatingContext;

/**
 * Created by lizwu on 5/22/14.
 */
public interface Processor {
    void process(SubsRatingContext context);
}
