/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.Price;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.PromotionResource;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.spec.fusion.RatingOffer;
import com.junbo.rating.spec.fusion.RatingPrice;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizwu on 2/25/14.
 */
public class CatalogGatewayImpl implements CatalogGateway{
    @Autowired
    private OfferResource offerResource;

    @Autowired
    private PromotionResource promotionResource;

    @Override
    public RatingOffer getOffer(Long offerId) {
        Offer offer;
        try {
            offer = offerResource.getOffer(offerId, EntityGetOptions.getDefault()).wrapped().get();
        } catch (Exception e) {
            //TODO: throw pre-defined exception
            throw new RuntimeException(e);
        }

        RatingOffer result = new RatingOffer();
        result.setId(offer.getId());
        result.getCategories().addAll(offer.getCategories());
        for (String country : offer.getPrices().keySet()) {
            Price price = offer.getPrices().get(country);
            RatingPrice ratingPrice = new RatingPrice(price.getAmount(), price.getCurrency());
            result.getPrices().put(country, ratingPrice);
        }
        return result;
    }

    @Override
    public List<Promotion> getPromotions() {
        List<Promotion> results = new ArrayList<Promotion>();

        EntitiesGetOptions options = new EntitiesGetOptions();
        options.setStart(Constants.DEFAULT_PAGE_START);
        options.setSize(Constants.DEFAULT_PAGE_SIZE);

        while(true) {
            List<Promotion> promotions = new ArrayList<Promotion>();
            try {
                promotions.addAll(promotionResource.getPromotions(options).wrapped().get().getResults());
            } catch (Exception e) {
                //TODO: throw pre-defined exception
                throw new RuntimeException(e);
            }
            results.addAll(promotions);
            options.setStart(options.getSize() + options.getSize());
            if (promotions.size()<Constants.DEFAULT_PAGE_SIZE) {
                break;
            }
        }

        return results;
    }
}
