/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.catalog.spec.resource.EntitlementDefinitionResource;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.model.Results;
import com.junbo.fulfilment.clientproxy.CatalogGateway;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.spec.error.AppErrors;
import com.junbo.fulfilment.spec.fusion.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CatalogGatewayImpl.
 */
public class CatalogGatewayImpl implements CatalogGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogGatewayImpl.class);

    @Autowired
    @Qualifier("offerRevisionClient")
    private OfferRevisionResource offerRevisionResource;

    @Autowired
    @Qualifier("itemRevisionClient")
    private ItemRevisionResource itemRevisionResource;

    @Autowired
    @Qualifier("entitlementDefClient")
    private EntitlementDefinitionResource entitlementDefClient;

    @Override
    public Offer getOffer(Long offerId, Long timestamp) {
        return wash(retrieveOfferRevision(offerId, timestamp));
    }

    @Override
    public Item getItem(Long itemId, Long timestamp) {
        return wash(retrieveItemRevision(itemId, timestamp));
    }

    @Override
    public ShippingMethod getShippingMethod(Long shippingMethodId) {
        return new ShippingMethod();
    }

    protected OfferRevision retrieveOfferRevision(Long offerId, Long timestamp) {
        try {
            OfferRevisionsGetOptions options = new OfferRevisionsGetOptions();
            options.setOfferIds(Arrays.asList(new OfferId(offerId)));
            options.setTimestamp(timestamp);

            Results<OfferRevision> revisions = offerRevisionResource.getOfferRevisions(options).wrapped().get();

            if (revisions == null || CollectionUtils.isEmpty(revisions.getItems())) {
                LOGGER.error("Offer [" + offerId + "] with timestamp [" + timestamp + "] does not exist");
                throw AppErrors.INSTANCE.notFound("Offer", offerId).exception();
            }

            return revisions.getItems().get(Constant.UNIQUE_RESULT);
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Catalog] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("catalog").exception();
        }
    }

    protected ItemRevision retrieveItemRevision(Long itemId, Long timestamp) {
        try {
            ItemRevisionsGetOptions options = new ItemRevisionsGetOptions();
            options.setItemIds(Arrays.asList(new ItemId(itemId)));
            options.setTimestamp(timestamp);

            Results<ItemRevision> revisions = itemRevisionResource.getItemRevisions(options).wrapped().get();

            if (revisions == null || CollectionUtils.isEmpty(revisions.getItems())) {
                LOGGER.error("Item [" + itemId + "] with timestamp [" + timestamp + "] does not exist");
                throw AppErrors.INSTANCE.notFound("Item", itemId).exception();
            }

            return revisions.getItems().get(Constant.UNIQUE_RESULT);
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Catalog] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("catalog").exception();
        }
    }

    protected Offer wash(OfferRevision offerRevision) {
        Offer result = new Offer();

        // fill offer base info
        result.setOfferId(offerRevision.getOfferId());

        // fill sub offers info
        if (offerRevision.getSubOffers() != null) {
            for (Long subOfferId : offerRevision.getSubOffers()) {
                LinkedEntry subOffer = new LinkedEntry();

                subOffer.setEntityType(CatalogEntityType.OFFER);
                subOffer.setId(subOfferId);
                subOffer.setQuantity(1);

                result.addSubOffer(subOffer);
            }
        }

        // fill items info
        if (offerRevision.getItems() != null) {
            for (ItemEntry entry : offerRevision.getItems()) {
                LinkedEntry item = new LinkedEntry();

                item.setEntityType(CatalogEntityType.ITEM);
                item.setId(entry.getItemId());
                item.setQuantity(entry.getQuantity());

                result.addItem(item);
            }
        }

        // fill fulfilment actions
        if (offerRevision.getEventActions() != null) {
            List<Action> actions = offerRevision.getEventActions().get(Constant.EVENT_PURCHASE);

            if (actions != null) {
                for (Action action : actions) {
                    OfferAction offerAction = new OfferAction();
                    offerAction.setType(action.getType());
                    offerAction.setProperties(buildActionProperties(action));

                    result.addFulfilmentAction(offerAction);
                }
            }
        }

        return result;
    }

    protected Item wash(ItemRevision itemRevision) {
        Item item = new Item();
        item.setItemId(itemRevision.getItemId());
        item.setSku(itemRevision.getSku());

        return item;
    }

    private Map<String, Object> buildActionProperties(Action action) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constant.ENTITLEMENT_DEF_ID, action.getEntitlementDefId());
        result.put(Constant.ITEM_ID, action.getItemId());
        result.put(Constant.STORED_VALUE_CURRENCY, action.getStoredValueCurrency());
        result.put(Constant.STORED_VALUE_AMOUNT, action.getStoredValueAmount());

        return result;
    }
}
