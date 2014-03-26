/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.common.model.Results;

import java.util.List;

class OfferResultList extends Results<Offer> {
    @Override
    public List<Offer> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<Offer> results) {
        super.setItems(results);
    }
}
class ItemResultList extends Results<Item> {
    @Override
    public List<Item> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<Item> results) {
        super.setItems(results);
    }
}
class AttributeResultList extends Results<Attribute> {
    @Override
    public List<Attribute> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<Attribute> results) {
        super.setItems(results);
    }
}
class EntitlementDefinitionResultList extends Results<EntitlementDefinition> {
    @Override
    public List<EntitlementDefinition> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<EntitlementDefinition> results) {
        super.setItems(results);
    }
}
class PromotionResultList extends Results<Promotion> {
    @Override
    public List<Promotion> getItems() {
        return super.getItems();
    }

    @Override
    public void setItems(List<Promotion> results) {
        super.setItems(results);
    }
}
