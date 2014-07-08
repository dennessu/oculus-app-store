/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.service;

import com.junbo.rating.core.RatingService;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.core.processor.ProcessorRegister;

/**
 * Subscription Rating Service.
 */

public class SubsRatingService implements RatingService<SubsRatingContext> {
    @Override
    public void rate(SubsRatingContext context) {
        ProcessorRegister.getProcessor(context.getSubsRatingType().toString()).process(context);
    }
}
