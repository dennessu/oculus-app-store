/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.handler;

import com.junbo.catalog.spec.model.promotion.Criterion;
import com.junbo.rating.core.context.RatingContext;

/**
 * Created by lizwu on 2/21/14.
 * @param <T> different criteria of promotion
 */
public interface CriterionHandler<T extends Criterion> {
    boolean validate(T criterion, RatingContext context);
}
