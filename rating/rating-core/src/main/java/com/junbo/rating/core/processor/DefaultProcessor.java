/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.spec.fusion.OfferAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by lizwu on 5/22/14.
 */
public class DefaultProcessor extends ProcessorSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProcessor.class);

    @Override
    public void process(SubsRatingContext context) {
        List<OfferAction> actions = getActions(context, Constants.PURCHASE_EVENT, Constants.CHARGE_ACTION);
        context.setAmount(getPrice(actions, context));
    }
}
