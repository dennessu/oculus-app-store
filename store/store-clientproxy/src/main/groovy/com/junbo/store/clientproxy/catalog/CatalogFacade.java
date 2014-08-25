/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.catalog;

import com.junbo.common.enumid.LocaleId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.catalog.Category;
import com.junbo.store.spec.model.catalog.Item;
import com.junbo.store.spec.model.catalog.Offer;
import com.junbo.store.spec.model.iap.IAPOfferGetRequest;

import java.util.List;

/**
 * The CatalogFacade class.
 */
public interface CatalogFacade {

    Promise<Item> getItem(String itemId);

    Promise<Offer> getOffer(String offerId, LocaleId locale);

    Promise<Results<Item>> getItemsByCategory(String categoryId, Long cursor, Long count);

    Promise<Item> getItemByPackageName(String packageName);

    Promise<Category> getCategory(String categoryId);

    Promise<List<Category>> getCategoriesByParent(String parentId);

    Promise<List<Offer>> getInAppOffers(Item hostItem, IAPOfferGetRequest request);
}
