/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.docs.app.resultlists;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.common.ResultList;
import com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.promotion.Promotion;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * The non-generic ResultList types for catalog.
 */
public class CatalogResultLists {

    /**
     * Find the non-generic ResultList type.
     */
    public static Class getClass(ParameterizedType type) {
        Type actualType = type.getActualTypeArguments()[0];
        return resultListMap.get(actualType);
    }

    private CatalogResultLists() {}
    private static Map<Class, Class> resultListMap = ResultListUtils.getMap(
            OfferResultList.class,
            ItemResultList.class,
            AttributeResultList.class,
            EntitlementDefinitionResultList.class,
            PromotionResultList.class);
}

class OfferResultList extends ResultList<Offer> {
    @Override
    public List<Offer> getResults() {
        return super.getResults();
    }

    @Override
    public void setResults(List<Offer> results) {
        super.setResults(results);
    }
}
class ItemResultList extends ResultList<Item> {
    @Override
    public List<Item> getResults() {
        return super.getResults();
    }

    @Override
    public void setResults(List<Item> results) {
        super.setResults(results);
    }
}
class AttributeResultList extends ResultList<Attribute> {
    @Override
    public List<Attribute> getResults() {
        return super.getResults();
    }

    @Override
    public void setResults(List<Attribute> results) {
        super.setResults(results);
    }
}
class EntitlementDefinitionResultList extends ResultList<EntitlementDefinition> {
    @Override
    public List<EntitlementDefinition> getResults() {
        return super.getResults();
    }

    @Override
    public void setResults(List<EntitlementDefinition> results) {
        super.setResults(results);
    }
}
class PromotionResultList extends ResultList<Promotion> {
    @Override
    public List<Promotion> getResults() {
        return super.getResults();
    }

    @Override
    public void setResults(List<Promotion> results) {
        super.setResults(results);
    }
}
