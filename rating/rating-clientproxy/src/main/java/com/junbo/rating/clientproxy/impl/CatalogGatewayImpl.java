/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy.impl;

import com.junbo.catalog.spec.enums.PriceType;
import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.catalog.spec.resource.*;
import com.junbo.rating.clientproxy.CatalogGateway;
import com.junbo.rating.common.util.Constants;
import com.junbo.rating.common.util.Utils;
import com.junbo.rating.spec.error.AppErrors;
import com.junbo.rating.spec.fusion.*;
import com.junbo.rating.spec.fusion.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.util.*;

//import com.junbo.catalog.spec.model.common.*;
//import com.junbo.catalog.spec.model.promotion.Promotion;
//import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
//import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;

/**
 * Catalog gateway.
 */
public class CatalogGatewayImpl implements CatalogGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogGatewayImpl.class);

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

    @Override
    public Item getItem(String itemId) {
        try {
            return itemResource.getItem(itemId).get();
        } catch (Exception e) {
            LOGGER.error("Error occurring when getting Item [" + itemId + "].", e);
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }
    }

    @Override
    public RatingOffer getOffer(String offerId, String timestamp) {
        Offer offer = retrieveOffer(offerId);

        OfferRevision offerRevision = timestamp == null ?
                getCurrentRevision(offer.getCurrentRevisionId())
                : getOfferRevisionByTimestamp(offerId, Utils.parseDateTime(timestamp));

        RatingOffer result = wash(offerRevision);
        result.setDeveloperRatio(offer.getDeveloperRatio() == null ? BigDecimal.ZERO : offer.getDeveloperRatio());
        result.setId(offerId);
        if (offer.getCategories() != null) {
            result.getCategories().addAll(offer.getCategories());
        }

        return result;
    }

    @Override
    public List<PromotionRevision> getPromotions() {
        List<PromotionRevision> results = new ArrayList<>();

            /*PromotionsGetOptions options = new PromotionsGetOptions();
            options.setStart(Constants.DEFAULT_PAGE_START);
            options.setSize(Constants.DEFAULT_PAGE_SIZE);

            List<PromotionRevisionId> revisionIds = new ArrayList<>();
            while(true) {
                List<Promotion> promotions = new ArrayList<>();
                try {
                    promotions.addAll(promotionResource.getPromotions(options).get().getItems());
                } catch (Exception e) {
                    LOGGER.error("Error occurring when getting promotions.", e);
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
                LOGGER.info("No promotion exists in Catalog component.");
                return results;
            }

            PromotionRevisionsGetOptions revisionOptions = new PromotionRevisionsGetOptions();
            revisionOptions.setRevisionIds(revisionIds);
            try {
                results.addAll(promotionRevisionResource.getPromotionRevisions(revisionOptions).get().getItems());
            } catch (Exception e) {
                LOGGER.error("Error occurring when getting Promotion Revisions.", e);
                throw AppErrors.INSTANCE.catalogGatewayError().exception();
            }*/

        return results;
    }

    @Override
    public ShippingMethod getShippingMethod(String shippingMethodId) {
        return null;
    }

    private Offer retrieveOffer(String offerId) {
        Offer offer;
        try {
            offer = offerResource.getOffer(offerId).get();
        } catch (Exception e) {
            LOGGER.error("Error occurring when getting Offer [" + offerId + "].", e);
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }
        return offer;
    }

    private OfferRevision getCurrentRevision(String revisionId) {
        OfferRevision offerRevision;
        try {
            offerRevision = offerRevisionResource.getOfferRevision(revisionId, new OfferRevisionGetOptions()).get();
        } catch (Exception e) {
            LOGGER.error("Error occurring when getting Offer Revision [" + revisionId + "]", e);
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }

        return offerRevision;
    }

    private OfferRevision getOfferRevisionByTimestamp(String offerId, Long timestamp) {
        List<OfferRevision> revisions = new ArrayList<>();

        OfferRevisionsGetOptions options = new OfferRevisionsGetOptions();
        options.setOfferIds(new HashSet<>(Arrays.asList(offerId)));
        options.setTimestamp(timestamp);

        try {
            revisions.addAll(offerRevisionResource.getOfferRevisions(options).get().getItems());
        } catch (Exception e) {
            LOGGER.error("Error occurring when getting Offer Revision " +
                    "with offerId [" + offerId + "] and timestamp [" + timestamp + "].", e);
            throw AppErrors.INSTANCE.catalogGatewayError().exception();
        }

        if (revisions.isEmpty()) {
            LOGGER.error("Revision with offerId [" + offerId + "] and timestamp [" + timestamp + "] does not exist.");
            throw AppErrors.INSTANCE.offerRevisionNotFound(offerId).exception();
        }

        return revisions.get(Constants.UNIQUE);
    }

    private Price getPrice(com.junbo.catalog.spec.model.common.Price price) {
        if (price == null) {
            return null;
        }
        Map<String, Map<String, BigDecimal>> prices = new HashMap<>();
        switch (PriceType.valueOf(price.getPriceType())) {
            case CUSTOM:
                prices.putAll(price.getPrices());
                break;
            case TIERED:
                PriceTier priceTier;
                try {
                    priceTier = priceTierResource.getPriceTier(price.getPriceTier()).get();
                } catch (Exception e) {
                    throw AppErrors.INSTANCE.catalogGatewayError().exception();
                }
                prices.putAll(priceTier.getPrices());
                break;
        }

        return new Price(price.getPriceType(), prices);
    }

    private RatingOffer wash(OfferRevision offerRevision) {
        RatingOffer offer = new RatingOffer();

        offer.setPrice(getPrice(offerRevision.getPrice()));
        offer.setPreOrderPrice(getPrice(offerRevision.getPreOrderPrice()));

        for (String country : offerRevision.getCountries().keySet()) {
            CountryProperties properties = offerRevision.getCountries().get(country);
            offer.getCountries().put(country, new Properties(properties.getIsPurchasable(), properties.getReleaseDate()));
        }

        if (offerRevision.getItems() != null) {
            for (ItemEntry entry : offerRevision.getItems()) {
                LinkedEntry item = new LinkedEntry();
                item.setEntryId(entry.getItemId());
                item.setType(EntryType.ITEM);
                item.setQuantity(entry.getQuantity() == null ? 1 : entry.getQuantity());
                offer.getItems().add(item);
            }
        }

        if (offerRevision.getSubOffers() != null) {
            for (String entry : offerRevision.getSubOffers()) {
                LinkedEntry subOffer = new LinkedEntry();
                subOffer.setEntryId(entry);
                subOffer.setType(EntryType.OFFER);
                offer.getSubOffers().add(subOffer);
            }
        }

        if (offerRevision.getEventActions() != null) {
            for (String eventType : offerRevision.getEventActions().keySet()) {
                List<OfferAction> offerActions = new ArrayList<>();
                List<Action> actions = offerRevision.getEventActions().get(eventType);
                for (Action action : actions) {
                    OfferAction offerAction = new OfferAction();
                    offerAction.setType(action.getType());
                    offerAction.setConditions(buildActionCondition(action.getCondition()));
                    offerAction.setPrice(getPrice(action.getPrice()));
                    offerActions.add(offerAction);
                }
                offer.getEventActions().put(eventType, offerActions);
            }
        }

        return offer;
    }

    private Map<String, Object> buildActionCondition(ActionCondition condition) {
        Map<String, Object> result = new HashMap<>();
        if (condition == null) {
            return result;
        }

        result.put(Constants.FROM_CYCLE, condition.getFromCycle());
        result.put(Constants.TO_CYCLE, condition.getToCycle());
        result.put(Constants.EXTEND_DURATION, condition.getExtendDuration());
        result.put(Constants.EXTEND_DURATION_UNIT, condition.getExtendDurationUnit());

        return result;
    }
}
