/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferEntry;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import com.junbo.catalog.spec.resource.AttributeResource;
import com.junbo.catalog.spec.resource.ItemResource;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.catalog.spec.resource.PromotionResource;
import com.junbo.common.id.AttributeId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.fusion.EntryType;
import com.junbo.rating.spec.fusion.LinkedEntry;
import com.junbo.rating.spec.fusion.Price;
import com.junbo.rating.spec.fusion.RatingOffer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalog gateway.
 */
public class CatalogGatewayImpl implements CatalogGateway{
    @Autowired
    private AttributeResource attributeResource;

    @Autowired
    private ItemResource itemResource;

    @Autowired
    private OfferResource offerResource;

    @Autowired
    private PromotionResource promotionResource;

    @Override
    public Attribute getAttribute(Long attributeId) {
        try {
            return attributeResource.getAttribute(new AttributeId(attributeId)).wrapped().get();
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }
    }

    @Override
    public Item getItem(Long itemId) {
        try {
            return itemResource.getItem(new ItemId(itemId), EntityGetOptions.getDefault()).wrapped().get();
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }
    }

    @Override
    public RatingOffer getOffer(Long offerId) {
        Offer offer;
        try {
            offer = offerResource.getOffer(new OfferId(offerId), EntityGetOptions.getDefault()).wrapped().get();
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }

        RatingOffer result = new RatingOffer();
        result.setId(offer.getId());
        if (offer.getCategories() != null) {
            result.getCategories().addAll(offer.getCategories());
        }
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

        PromotionsGetOptions options = new PromotionsGetOptions();
        options.setStart(Constants.DEFAULT_PAGE_START);
        options.setSize(Constants.DEFAULT_PAGE_SIZE);

        while(true) {
            List<Promotion> promotions = new ArrayList<Promotion>();
            try {
                promotions.addAll(promotionResource.getPromotions(options).wrapped().get().getResults());
            } catch (Exception e) {
                throw AppErrors.INSTANCE.catalogGatewayError().exception();
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
