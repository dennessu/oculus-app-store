/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.resource.EntitlementDefinitionResource;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.model.Results;
import com.junbo.fulfilment.clientproxy.CatalogGateway;
import com.junbo.fulfilment.common.collection.SevereMap;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.spec.error.AppErrors;
import com.junbo.fulfilment.spec.fusion.*;
import com.junbo.fulfilment.spec.fusion.Offer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
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
        if (offerRevision.getEvents() != null) {
            Event purchaseEvent = offerRevision.getEvents().get(Constant.EVENT_PURCHASE.toLowerCase());

            if (purchaseEvent != null && purchaseEvent.getActions() != null) {
                for (Action action : purchaseEvent.getActions()) {
                    OfferAction offerAction = new OfferAction();
                    offerAction.setType(action.getType());

                    Map<String, Object> entitlementDef = getEntitlementDef(action);
                    offerAction.setProperties(new SevereMap<>(entitlementDef));

                    // fill item info for physical delivery action
                    offerAction.setItems(result.getItems());

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

        //TODO
        item.setEwalletAmount(null);
        item.setEwalletCurrency(null);
        item.setEwalletCurrencyType(null);

        return item;
    }

    protected Map<String, Object> getEntitlementDef(Action action) {
        Map<String, Object> result = new HashMap<>();

        Long entitlementDefId = action.getEntitlementDefId();
        if (entitlementDefId == null) {
            return result;
        }

        try {
            EntitlementDefinition entitlementDef = entitlementDefClient.getEntitlementDefinition(
                    new EntitlementDefinitionId(entitlementDefId)).wrapped().get();

            if (entitlementDef == null) {
                throw AppErrors.INSTANCE.notFound("EntitlementDefinition", entitlementDefId).exception();
            }

            result.put(Constant.ENTITLEMENT_GROUP, entitlementDef.getGroup());
            result.put(Constant.ENTITLEMENT_TAG, entitlementDef.getTag());
            result.put(Constant.ENTITLEMENT_TYPE, entitlementDef.getType());
            result.put(Constant.ENTITLEMENT_DEVELOPER, entitlementDef.getDeveloperId());
            result.put(Constant.ENTITLEMENT_DEF_ID, entitlementDefId);

            return result;
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Catalog] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("catalog").exception();
        }
    }
}
