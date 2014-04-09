/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.db.repo.OfferRevisionRepository;
import com.junbo.catalog.spec.error.AppErrors;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemType;
import com.junbo.catalog.spec.model.offer.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl extends BaseRevisionedServiceImpl<Offer, OfferRevision> implements OfferService {
    @Autowired
    private OfferRepository offerRepo;
    @Autowired
    private OfferRevisionRepository offerRevisionRepo;
    @Autowired
    private ItemService itemService;

    @Override
    public Offer createEntity(Offer offer) {
        if (Boolean.TRUE.equals(offer.getCurated())) {
            throw AppErrors.INSTANCE
                    .fieldNotCorrect("curated", "Cannot create an offer with curated true.").exception();
        }
        validateOffer(offer);
        return super.createEntity(offer);
    }

    @Override
    public Offer updateEntity(Long offerId, Offer offer) {
        validateId(offerId, offer.getOfferId());
        validateOffer(offer);
        return super.updateEntity(offerId, offer);
    }

    @Override
    public OfferRevision createRevision(OfferRevision revision) {
        validateRevision(revision);
        generateDownloadEntitlement(revision);
        return super.createRevision(revision);
    }

    @Override
    public OfferRevision updateRevision(Long revisionId, OfferRevision revision) {
        validateId(revisionId, revision.getRevisionId());
        validateRevision(revision);
        generateDownloadEntitlement(revision);

        return super.updateRevision(revisionId, revision);
    }

    @Override
    protected OfferRepository getEntityRepo() {
        return offerRepo;
    }

    @Override
    protected OfferRevisionRepository getRevisionRepo() {
        return offerRevisionRepo;
    }

    @Override
    protected String getRevisionType() {
        return "offer-revision";
    }

    @Override
    protected String getEntityType() {
        return "offer";
    }

    private void validateOffer(Offer offer) {
        checkFieldNotNull(offer.getOwnerId(), "offerId");
        checkFieldNotEmpty(offer.getName(), "name");
        checkFieldNotNull(offer.getOwnerId(), "publisher");
    }

    private void validateRevision(OfferRevision revision) {
        checkFieldNotNull(revision.getOwnerId(), "publisher");
        checkFieldNotNull(revision.getOfferId(), "offerId");
        if (!PriceType.ALL_TYPES.contains(revision.getPriceType())) {
            throw AppErrors.INSTANCE
                    .fieldNotCorrect("priceType", "Valid price types: " + PriceType.ALL_TYPES).exception();
        }
        checkPrice(revision);
    }
    private void checkPrice(OfferRevision revision) {
        if (PriceType.TIERED.equals(revision.getPriceType())) {
            checkFieldShouldEmpty(revision.getPrices(), "prices");
        } else if (PriceType.FREE.equals(revision.getPriceType())) {
            checkFieldShouldNull(revision.getPriceTierId(), "priceTier");
            checkFieldShouldEmpty(revision.getPrices(), "prices");
        } else if (PriceType.CUSTOM.equals(revision.getPriceType())) {
            checkFieldShouldNull(revision.getPriceTierId(), "priceTier");
        }
    }

    private Event preparePurchaseEvent(OfferRevision revision) {
        if (revision.getEvents()==null) {
            revision.setEvents(new HashMap<String, Event>());
        }
        Event event = revision.getEvents().get(EventType.PURCHASE);
        if (event == null) {
            event = new Event();
            event.setName(EventType.PURCHASE);
            event.setActions(new ArrayList<Action>());
            revision.getEvents().put(EventType.PURCHASE, event);
        }

        return event;
    }

    private void removeAutoGeneratedActions(Event purchaseEvent) {
        List<Action> autoGeneratedActions = new ArrayList<>();
        for (Action action : purchaseEvent.getActions()) {
            if (Boolean.TRUE.equals(action.isAutoGenerated())) {
                autoGeneratedActions.add(action);
            }
        }
        purchaseEvent.getActions().removeAll(autoGeneratedActions);
    }

    private void generateDownloadEntitlement(OfferRevision revision) {
        Event purchaseEvent = preparePurchaseEvent(revision);
        removeAutoGeneratedActions(purchaseEvent);

        for (ItemEntry itemEntry : revision.getItems()) {
            Item item = itemService.getEntity(itemEntry.getItemId());
            checkEntityNotNull(itemEntry.getItemId(), item, "item");
            if (ItemType.DIGITAL.equalsIgnoreCase(item.getType())) {
                Action action = new Action();
                action.setAutoGenerated(true);
                action.setType(ActionType.GRANT_ENTITLEMENT);
                action.setEntitlementDefId(item.getEntitlementDefId());
                purchaseEvent.getActions().add(action);
            }
        }
    }
}
