/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.offer.*;
import com.junbo.catalog.spec.resource.OfferResource;
import com.junbo.common.id.OfferId;
import com.junbo.fulfilment.clientproxy.CatalogGateway;
import com.junbo.fulfilment.common.collection.SevereMap;
import com.junbo.fulfilment.common.exception.CatalogGatewayException;
import com.junbo.fulfilment.common.exception.ResourceNotFoundException;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.fusion.*;
import com.junbo.fulfilment.spec.fusion.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * CatalogGatewayImpl.
 */
public class CatalogGatewayImpl implements CatalogGateway {
    private static final String PURCHASE_EVENT = "PURCHASE_EVENT";
    private static final String OFFER_RELEASED_STATUS = "RELEASED";
    private static final Long OFFER_TIMESTAMP_NOT_SPECIFIED = null;

    @Autowired
    @Qualifier("offerClient")
    private OfferResource offerResource;

    @Override
    public Offer getOffer(Long offerId, Long timestamp) {
        return wash(retrieve(offerId, timestamp));
    }

    @Override
    public Offer getOffer(Long offerId) {
        return wash(retrieve(offerId, OFFER_TIMESTAMP_NOT_SPECIFIED));
    }


    @Override
    public ShippingMethod getShippingMethod(Long shippingMethodId) {
        //TODO
        return new ShippingMethod();
    }

    protected com.junbo.catalog.spec.model.offer.Offer retrieve(Long offerId, Long timestamp) {
        try {
            EntityGetOptions options = EntityGetOptions.getDefault();
            options.setStatus(OFFER_RELEASED_STATUS);

            com.junbo.catalog.spec.model.offer.Offer offer =
                    offerResource.getOffer(new OfferId(offerId), options).wrapped().get();

            if (offer == null) {
                throw new ResourceNotFoundException(
                        "Offer [" + offerId + "] with timestamp [" + timestamp + "] does not exist");
            }

            return offer;
        } catch (Exception e) {
            throw new CatalogGatewayException("Error occurred during calling [Catalog] component service.", e);
        }
    }

    protected Offer wash(com.junbo.catalog.spec.model.offer.Offer offer) {
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
        for (Event event : offer.getEvents()) {
            if (!Utils.equals(PURCHASE_EVENT, event.getName())) {
                continue;
            }

            for (Action action : event.getActions()) {
                OfferAction offerAction = new OfferAction();
                offerAction.setType(action.getType());
                offerAction.setProperties(new SevereMap<>(action.getProperties()));

                // fill item info for physical delivery action
                offerAction.setItems(result.getItems());

                result.addFulfilmentAction(offerAction);
            }
        }

        return result;
    }
}
