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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lizwu on 5/22/14.
 */
public abstract class ProcessorSupport implements Processor{
    @Autowired
    @Qualifier("ratingCatalogGateway")
    private CatalogGateway catalogGateway;

    protected BigDecimal getPrice(SubsRatingContext context, String eventType, String actionType) {
        RatingOffer offer = catalogGateway.getOffer(context.getOfferId(), null);
        List<OfferAction> actions = offer.getEventActions().get(eventType);

        for (OfferAction action : actions) {
            if (actionType.equalsIgnoreCase(action.getType())) {
                return retrievePrice(action.getPrice(), context.getCurrency().getCode());
            }
        }

        throw AppErrors.INSTANCE.actionNotFound(actionType).exception();
    }

    private BigDecimal retrievePrice(Price price, String currency) {
        if (price == null) {
            throw AppErrors.INSTANCE.priceNotFound().exception();
        }

        if (PriceType.FREE.name().equalsIgnoreCase(price.getPriceType())) {
            return BigDecimal.ZERO;
        }

        if (!price.getPrices().containsKey(currency)) {
            throw AppErrors.INSTANCE.priceNotFound().exception();
        }

        return price.getPrices().get(currency);
    }
}
