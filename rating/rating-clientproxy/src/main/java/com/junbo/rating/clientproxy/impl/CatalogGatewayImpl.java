/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferEntry;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.PromotionResource;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.spec.fusion.EntryType;
import com.junbo.rating.spec.fusion.LinkedEntry;
import com.junbo.rating.spec.fusion.Price;
import com.junbo.rating.spec.fusion.RatingOffer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizwu on 2/25/14.
 */
public class CatalogGatewayImpl implements CatalogGateway{
    @Autowired
    private ItemResource itemResource;

    @Autowired
    private OfferResource offerResource;

    @Autowired
    private PromotionResource promotionResource;

    @Override
    public Item getItem(Long itemId) {
        try {
            return itemResource.getItem(itemId, EntityGetOptions.getDefault()).wrapped().get();
        } catch (Exception e) {
            //TODO: throw pre-defined exception
            throw new RuntimeException(e);
        }
    }

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
            com.junbo.catalog.spec.model.offer.Price price = offer.getPrices().get(country);
            Price ratingPrice = new Price(price.getAmount(), price.getCurrency());
            result.getPrices().put(country, ratingPrice);
        }

        if (offer.getItems() != null) {
            for (ItemEntry entry : offer.getItems()) {
                LinkedEntry item = new LinkedEntry();
                item.setEntryId(entry.getItemId());
                item.setType(EntryType.ITEM);
                item.setQuantity(entry.getQuantity());
                result.getItems().add(item);
            }
        }

        if (offer.getSubOffers() != null) {
            for (OfferEntry entry : offer.getSubOffers()) {
                LinkedEntry subOffer = new LinkedEntry();
                subOffer.setEntryId(entry.getOfferId());
                subOffer.setType(EntryType.OFFER);
                subOffer.setQuantity(entry.getQuantity());
                result.getSubOffers().add(subOffer);
            }
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

    @Override
    public ShippingMethod getShippingMethod(Long shippingMethodId) {
        return null;
    }
}
