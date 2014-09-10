/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.item.EntitlementDef;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.ItemEntry;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.catalog.spec.resource.ItemRevisionResource;
import com.junbo.catalog.spec.resource.OfferRevisionResource;
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

import java.util.*;

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

    @Override
    public Offer getOffer(String offerId, Long timestamp) {
        return wash(retrieveOfferRevision(offerId, timestamp));
    }

    @Override
    public Item getItem(String itemId, Long timestamp) {
        return wash(retrieveItemRevision(itemId, timestamp));
    }

    @Override
    public ShippingMethod getShippingMethod(String shippingMethodId) {
        return new ShippingMethod();
    }

    protected OfferRevision retrieveOfferRevision(String offerId, Long timestamp) {
        try {
            OfferRevisionsGetOptions options = new OfferRevisionsGetOptions();
            options.setOfferIds(new HashSet(Arrays.asList(offerId)));
            options.setTimestamp(timestamp);

            Results<OfferRevision> revisions = offerRevisionResource.getOfferRevisions(options).get();

            if (revisions == null || CollectionUtils.isEmpty(revisions.getItems())) {
                LOGGER.error("Offer [" + offerId + "] with timestamp [" + timestamp + "] does not exist");
                throw AppErrors.INSTANCE.offerNotFound(offerId).exception();
            }

            return revisions.getItems().get(Constant.UNIQUE_RESULT);
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Catalog] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("Catalog").exception();
        }
    }

    protected ItemRevision retrieveItemRevision(String itemId, Long timestamp) {
        try {
            ItemRevisionsGetOptions options = new ItemRevisionsGetOptions();
            options.setItemIds(new HashSet(Arrays.asList(itemId)));
            options.setTimestamp(timestamp);

            Results<ItemRevision> revisions = itemRevisionResource.getItemRevisions(options).get();

            if (revisions == null || CollectionUtils.isEmpty(revisions.getItems())) {
                LOGGER.error("Item [" + itemId + "] with timestamp [" + timestamp + "] does not exist");
                throw AppErrors.INSTANCE.itemNotFound(itemId).exception();
            }

            return revisions.getItems().get(Constant.UNIQUE_RESULT);
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Catalog] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("Catalog").exception();
        }
    }

    protected Offer wash(OfferRevision offerRevision) {
        Offer result = new Offer();

        // fill offer base info
        result.setOfferId(offerRevision.getOfferId());

        // fill sub offers info
        if (offerRevision.getSubOffers() != null) {
            for (String subOfferId : offerRevision.getSubOffers()) {
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

                    offerAction.setItemId(action.getItemId());
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

        item.setEntitlementMetas(new ArrayList<EntitlementMeta>());

        if (itemRevision.getEntitlementDefs() != null) {
            for (EntitlementDef def : itemRevision.getEntitlementDefs()) {
                EntitlementMeta meta = new EntitlementMeta();
                meta.setConsumable(def.getConsumable());
                meta.setType(def.getType());

                item.getEntitlementMetas().add(meta);
            }
        }

        return item;
    }

    private Map<String, Object> buildActionProperties(Action action) {
        Map<String, Object> result = new HashMap<>();
        result.put(Constant.ITEM_ID, action.getItemId());
        result.put(Constant.STORED_VALUE_CURRENCY, action.getStoredValueCurrency());
        result.put(Constant.STORED_VALUE_AMOUNT, action.getStoredValueAmount());
        result.put(Constant.USE_COUNT, action.getUseCount());

        return result;
    }
}
