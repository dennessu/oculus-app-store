/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.spec.fusion;

import java.util.ArrayList;
import java.util.List;

/**
 * Offer.
 */
public class Offer {
    private Long offerId;
    private Integer revision;

    private List<OfferAction> actions;
    private List<LinkedEntry> items;
    private List<LinkedEntry> subOffers;

    public Offer() {
        actions = new ArrayList<OfferAction>();
        items = new ArrayList<LinkedEntry>();
        subOffers = new ArrayList<LinkedEntry>();
    }

    public void addItem(LinkedEntry item) {
        items.add(item);
    }

    public void addSubOffer(LinkedEntry subOffer) {
        subOffers.add(subOffer);
    }

    public void addFulfilmentAction(OfferAction action) {
        this.actions.add(action);
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public List<OfferAction> getActions() {
        return actions;
    }

    public void setActions(List<OfferAction> actions) {
        this.actions = actions;
    }

    public List<LinkedEntry> getItems() {
        return items;
    }

    public void setItems(List<LinkedEntry> items) {
        this.items = items;
    }

    public List<LinkedEntry> getSubOffers() {
        return subOffers;
    }

    public void setSubOffers(List<LinkedEntry> subOffers) {
        this.subOffers = subOffers;
    }
}
