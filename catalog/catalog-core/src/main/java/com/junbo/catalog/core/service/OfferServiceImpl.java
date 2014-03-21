/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.core.service;

import com.junbo.catalog.core.EntitlementDefinitionService;
import com.junbo.catalog.core.ItemService;
import com.junbo.catalog.core.OfferService;
import com.junbo.catalog.db.repo.OfferDraftRepository;
import com.junbo.catalog.db.repo.OfferRepository;
import com.junbo.catalog.spec.model.common.EntityGetOptions;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemType;
import com.junbo.catalog.spec.model.offer.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer service implementation.
 */
public class OfferServiceImpl extends BaseServiceImpl<Offer> implements OfferService {
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private OfferDraftRepository offerDraftRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private EntitlementDefinitionService entitlementDefService;

    @Override
    public OfferRepository getEntityRepo() {
        return offerRepository;
    }

    @Override
    public OfferDraftRepository getEntityDraftRepo() {
        return offerDraftRepository;
    }

    @Override
    public Offer create(Offer offer) {
        List<Action> actions = new ArrayList<>();
        for (ItemEntry itemEntry : offer.getItems()) {
            Item item = itemService.get(itemEntry.getItemId(), EntityGetOptions.getDefault());
            if (ItemType.APP.equalsIgnoreCase(item.getType())) {
                Action action = new Action();
                action.setType(ActionType.GRANT_ENTITLEMENT);
                action.setEntitlementDefId(item.getEntitlementDefId());
                actions.add(action);
            }
        }

        if (!actions.isEmpty()) {
            Event event = new Event();
            event.setName(EventType.PURCHASE);
            event.setActions(actions);
            offer.getEvents().add(event);
        }
        return super.create(offer);
    }

    @Override
    protected String getEntityType() {
        return "Offer";
    }
}
