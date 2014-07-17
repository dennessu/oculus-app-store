/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.core.processor;

import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.core.context.SubsRatingContext;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.fusion.OfferAction;
import com.junbo.rating.spec.fusion.Price;
import com.junbo.rating.spec.fusion.RatingOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lizwu on 5/22/14.
 */
public abstract class ProcessorSupport implements Processor{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorSupport.class);

    @Autowired
    @Qualifier("ratingCatalogGateway")
    private CatalogGateway catalogGateway;

    protected List<OfferAction> getActions(SubsRatingContext context, String eventType, String actionType) {
        List<OfferAction> results = new ArrayList<>();

        RatingOffer offer = catalogGateway.getOffer(context.getOfferId(), null);
        List<OfferAction> actions = offer.getEventActions().get(eventType);

        for (OfferAction action : actions) {
            if (actionType.equalsIgnoreCase(action.getType())) {
                results.add(action);
            }
        }

        if (results.isEmpty()) {
            LOGGER.error("Event [" + eventType + "] & Action [" + actionType +
                    "] is not configured in Offer [" + context.getOfferId() + "]");
            throw AppErrors.INSTANCE.missingConfiguration("action").exception();
        }

        return actions;
    }

    protected BigDecimal getPrice(List<OfferAction> actions, SubsRatingContext context) {
        for (OfferAction action : actions) {
            if (action.getConditions().isEmpty()) {
                return retrievePrice(action.getPrice(), context.getCountry(), context.getCurrency().getCurrencyCode());
            }
        }

        LOGGER.error("Cannot find price for subs offer [" + context.getOfferId() + "]");
        throw AppErrors.INSTANCE.missingConfiguration("action").exception();
    }

    protected BigDecimal retrievePrice(Price price, String country, String currency) {
        if (price == null) {
            throw AppErrors.INSTANCE.missingConfiguration("price").exception();
        }

        if (PriceType.FREE.name().equalsIgnoreCase(price.getPriceType())) {
            return BigDecimal.ZERO;
        }

        if (price.getPrices() == null || !price.getPrices().containsKey(country)) {
            throw AppErrors.INSTANCE.missingConfiguration("price").exception();
        }

        Map<String, BigDecimal> prices = price.getPrices().get(country);
        if (!prices.containsKey(currency)) {
            throw AppErrors.INSTANCE.missingConfiguration("price").exception();
        }

        return prices.get(currency);
    }
}
