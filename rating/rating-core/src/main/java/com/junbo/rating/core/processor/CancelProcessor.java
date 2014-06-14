/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.spec.fusion.OfferAction;

import java.util.List;

/**
 * Created by lizwu on 5/22/14.
 */
public class CancelProcessor extends ProcessorSupport {
    @Override
    public void process(SubsRatingContext context) {
        List<OfferAction> actions = getActions(context, Constants.CANCEL_EVENT, Constants.CHARGE_ACTION);
        context.setAmount(getPrice(actions, context));
    }
}
