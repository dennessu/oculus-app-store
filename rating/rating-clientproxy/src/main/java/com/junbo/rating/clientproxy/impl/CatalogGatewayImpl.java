/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import com.junbo.catalog.spec.resource.*;
import com.junbo.common.id.*;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.fusion.EntryType;
import com.junbo.rating.spec.fusion.LinkedEntry;
import com.junbo.rating.spec.fusion.Price;
import com.junbo.rating.spec.fusion.RatingOffer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.*;

/**
 * Catalog gateway.
 */
public class CatalogGatewayImpl implements CatalogGateway{
    @Autowired
    @Qualifier("ratingItemClient")
    private ItemResource itemResource;

    @Autowired
    @Qualifier("ratingOfferClient")
    private OfferResource offerResource;

    @Autowired
    @Qualifier("ratingOfferRevisionClient")
    private OfferRevisionResource offerRevisionResource;

    @Autowired
    private PriceTierResource priceTierResource;

    @Autowired
    private PromotionResource promotionResource;

    @Autowired
    private PromotionRevisionResource promotionRevisionResource;

    @Override
    public Item getItem(Long itemId) {
        try {
            return itemResource.getItem(new ItemId(itemId)).wrapped().get();
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }
    }

    @Override
    public RatingOffer getOffer(Long offerId, Long timestamp) {
        Offer offer;
        try {
            offer = offerResource.getOffer(new OfferId(offerId)).wrapped().get();
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }

        RatingOffer result = new RatingOffer();
        result.setId(offer.getOfferId());

        if (offer.getCategories() != null) {
            result.getCategories().addAll(offer.getCategories());
        }

        OfferRevision offerRevision;
        OfferRevisionsGetOptions options = new OfferRevisionsGetOptions();
        options.setOfferIds(Arrays.asList(new OfferId(offerId)));
        options.setTimestamp(timestamp);
        try {

            offerRevision = offerRevisionResource.getOfferRevisions(options).wrapped().get().getItems().get(0);
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }

        Map<String, BigDecimal> prices = new HashMap<>();
        switch(offerRevision.getPrice().getPriceType()) {
            case Price.CUSTOM:
                prices.putAll(offerRevision.getPrice().getPrices());
                break;
            case Price.TIERED:
                PriceTier priceTier;
                try {
                    priceTier = priceTierResource.getPriceTier(
                            new PriceTierId(offerRevision.getPrice().getPriceTier())).wrapped().get();
                } catch (Exception e) {
                    throw AppErrors.INSTANCE.catalogGatewayError().exception();
                }
                prices.putAll(priceTier.getPrices());
                break;
        }
        result.setPrice(new Price(offerRevision.getPrice().getPriceType(), prices));


        if (offerRevision.getItems() != null) {
            for (ItemEntry entry : offerRevision.getItems()) {
                LinkedEntry item = new LinkedEntry();
                item.setEntryId(entry.getItemId());
                item.setType(EntryType.ITEM);
                item.setQuantity(entry.getQuantity() == null? 1 : entry.getQuantity());
                result.getItems().add(item);
            }
        }

        if (offerRevision.getSubOffers() != null) {
            for (Long entry : offerRevision.getSubOffers()) {
                LinkedEntry subOffer = new LinkedEntry();
                subOffer.setEntryId(entry);
                subOffer.setType(EntryType.OFFER);
                result.getSubOffers().add(subOffer);
            }
        }
        return result;
    }

    @Override
    public List<PromotionRevision> getPromotions() {
        List<PromotionRevision> results = new ArrayList<>();

        PromotionsGetOptions options = new PromotionsGetOptions();
        options.setStart(Constants.DEFAULT_PAGE_START);
        options.setSize(Constants.DEFAULT_PAGE_SIZE);

        List<PromotionRevisionId> revisionIds = new ArrayList<>();
        while(true) {
            List<Promotion> promotions = new ArrayList<>();
            try {
                promotions.addAll(promotionResource.getPromotions(options).wrapped().get().getItems());
            } catch (Exception e) {
                throw AppErrors.INSTANCE.catalogGatewayError().exception();
            }

            for (Promotion promotion : promotions) {
                revisionIds.add(new PromotionRevisionId(promotion.getCurrentRevisionId()));
            }

            options.setStart(options.getSize() + options.getSize());
            if (promotions.size()<Constants.DEFAULT_PAGE_SIZE) {
                break;
            }
        }

        if (revisionIds.isEmpty()) {
            return results;
        }

        PromotionRevisionsGetOptions revisionOptions = new PromotionRevisionsGetOptions();
        revisionOptions.setRevisionIds(revisionIds);
        try {
            results.addAll(promotionRevisionResource.getPromotionRevisions(revisionOptions).wrapped().get().getItems());
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }

        return results;
    }

    @Override
    public ShippingMethod getShippingMethod(Long shippingMethodId) {
        return null;
    }
}
