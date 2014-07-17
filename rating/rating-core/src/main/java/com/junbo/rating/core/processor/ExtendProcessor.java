/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.rating.common.util.Constants;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.fusion.OfferAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by lizwu on 6/12/14.
 */
public class ExtendProcessor extends ProcessorSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtendProcessor.class);

    @Override
    public void process(SubsRatingContext context) {
        List<OfferAction> actions = getActions(context, Constants.PURCHASE_EVENT, Constants.CHARGE_ACTION);
        context.setAmount(getPrice(actions, context));
    }

    @Override
    protected BigDecimal getPrice(List<OfferAction> actions, SubsRatingContext context) {
        for (OfferAction action : actions) {
            if (action.getConditions().isEmpty()) {
                continue;
            }

            Map<String, Object> conditions = action.getConditions();
            if ((Integer) conditions.get(Constants.EXTEND_DURATION) == context.getExtensionNum()
                    && context.getExtensionUnit().toString().equalsIgnoreCase((String)conditions.get(Constants.EXTEND_DURATION_UNIT))) {
                return retrievePrice(action.getPrice(), context.getCountry(), context.getCurrency().getCurrencyCode());
            }
        }

        LOGGER.error("Cannot find price for subs offer [" + context.getOfferId() + "]");
        throw AppErrors.INSTANCE.missingConfiguration("action").exception();
    }
}
