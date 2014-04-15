/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.resource.EntitlementDefinitionResource;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
import com.junbo.common.id.EntitlementDefinitionId;
import com.junbo.common.id.OfferId;
import com.junbo.common.model.Results;
import com.junbo.fulfilment.clientproxy.CatalogGateway;
import com.junbo.fulfilment.common.collection.SevereMap;
import com.junbo.fulfilment.common.util.Constant;
import com.junbo.fulfilment.common.util.Utils;
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
    private static final String PURCHASE_EVENT = "PURCHASE";

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
        return wash(retrieve(offerId, timestamp));
    }

    @Override
    public ShippingMethod getShippingMethod(Long shippingMethodId) {
        return new ShippingMethod();
    }

    protected OfferRevision retrieve(Long offerId, Long timestamp) {
        try {
            OfferRevisionsGetOptions options = new OfferRevisionsGetOptions();
            options.setOfferIds(Arrays.asList(new OfferId(offerId)));
            //options.setTimestamp();

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

    protected Offer wash(OfferRevision offerRevision) {
        Offer result = new com.junbo.fulfilment.spec.fusion.Offer();

        // fill offer base info
        result.setOfferId(offer.getId());

        // fill sub offers info
        if (offer.getSubOffers() != null) {
            for (OfferEntry entry : offer.getSubOffers()) {
                LinkedEntry subOffer = new LinkedEntry();

                subOffer.setEntityType(CatalogEntityType.OFFER);
                subOffer.setId(entry.getOfferId());
                subOffer.setQuantity(entry.getQuantity());

                result.addSubOffer(subOffer);
            }
        }

        // fill items info
        if (offer.getItems() != null) {
            for (ItemEntry entry : offer.getItems()) {
                LinkedEntry item = new LinkedEntry();

                item.setEntityType(CatalogEntityType.ITEM);
                item.setId(entry.getItemId());
                item.setQuantity(entry.getQuantity());
                item.setSku(entry.getSku());

                result.addItem(item);
            }
        }

        // fill fulfilment actions
        if (offer.getEvents() != null) {
            for (Event event : offer.getEvents()) {
                if (!Utils.equals(PURCHASE_EVENT, event.getName())) {
                    continue;
                }

                if (event.getActions() == null) {
                    continue;
                }

                for (Action action : event.getActions()) {
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
