/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.SubsRatingContext;

import java.math.BigDecimal;

/**
 * Created by lizwu on 5/22/14.
 */
public class DefaultProcessor extends ProcessorSupport {
    @Override
    public void process(SubsRatingContext context) {
        BigDecimal amount = getPrice(context, Constants.PURCHASE_EVENT, Constants.CHARGE_ACTION);
        context.setAmount(amount);
    }
}
