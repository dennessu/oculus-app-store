/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefSearchParams;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
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
import com.junbo.rating.common.util.Utils;
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
    @Qualifier("priceTierClient")
    private PriceTierResource priceTierResource;

    @Autowired
    @Qualifier("promotionClient")
    private PromotionResource promotionResource;

    @Autowired
    @Qualifier("promotionRevisionClient")
    private PromotionRevisionResource promotionRevisionResource;

    @Autowired
    @Qualifier("entitlementDefinitionClient")
    private EntitlementDefinitionResource entitlementDefinitionResource;

    @Override
    public Item getItem(Long itemId) {
        try {
            return itemResource.getItem(new ItemId(itemId)).wrapped().get();
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }
    }

    @Override
    public RatingOffer getOffer(Long offerId, String timestamp) {
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
        if (timestamp == null) {
            try {
                offerRevision = offerRevisionResource.getOfferRevision(
                        new OfferRevisionId(offer.getCurrentRevisionId())).wrapped().get();
            } catch (Exception e) {
                throw AppErrors.INSTANCE.catalogGatewayError().exception();
            }
        } else {
            offerRevision = getOfferRevisionByTimestamp(offerId, Utils.parseDateTime(timestamp));
        }

        Map<String, BigDecimal> prices = new HashMap<>();
        switch(PriceType.valueOf(offerRevision.getPrice().getPriceType())) {
            case CUSTOM:
                prices.putAll(offerRevision.getPrice().getPrices());
                break;
            case TIERED:
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

            if (promotions.size() < Constants.DEFAULT_PAGE_SIZE) {
                break;
            }
            options.setStart(options.getSize() + Constants.DEFAULT_PAGE_SIZE);
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

    @Override
    public Map<Long, String> getEntitlementDefinitions(Set<String> groups) {
        Map<Long, String> result = new HashMap<>();

        EntitlementDefSearchParams searchParams = new EntitlementDefSearchParams();
        searchParams.setGroups(groups);

        PageableGetOptions options = new PageableGetOptions();
        options.setStart(Constants.DEFAULT_PAGE_START);
        options.setSize(Constants.DEFAULT_PAGE_SIZE);

        while(true) {
            List<EntitlementDefinition> entitlementDefinitions = new ArrayList<>();
            try {
                 entitlementDefinitions.addAll(
                         entitlementDefinitionResource.getEntitlementDefinitions(
                                 searchParams, options).wrapped().get().getItems());
            } catch (Exception e) {
                throw AppErrors.INSTANCE.catalogGatewayError().exception();
            }

            for (EntitlementDefinition entitlementDef : entitlementDefinitions) {
                result.put(entitlementDef.getEntitlementDefId(),
                        entitlementDef.getGroup() + Constants.ENTITLEMENT_SEPARATOR + entitlementDef.getTag());
            }

            if (entitlementDefinitions.size() < Constants.DEFAULT_PAGE_SIZE) {
                break;
            }
            options.setStart(options.getStart() + Constants.DEFAULT_PAGE_SIZE);
        }

        return result;
    }

    private OfferRevision getOfferRevisionByTimestamp(Long offerId, Long timestamp) {
        List<OfferRevision> revisions;
        OfferRevisionsGetOptions options = new OfferRevisionsGetOptions();
        options.setOfferIds(Arrays.asList(new OfferId(offerId)));
        options.setTimestamp(timestamp);
        try {
            revisions = offerRevisionResource.getOfferRevisions(options).wrapped().get().getItems();
        } catch (Exception e) {
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }

        if (revisions.isEmpty()) {
            throw AppErrors.INSTANCE.offerRevisionNotFound(offerId.toString()).exception();
        }

        return revisions.get(Constants.UNIQUE);
    }
}
