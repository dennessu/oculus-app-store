/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.catalog;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.ItemId;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.ApiContext;
import com.junbo.store.spec.model.browse.document.RevisionNote;
import com.junbo.store.spec.model.browse.document.Offer;

import java.util.List;

/**
 * The CatalogFacade class.
 */
public interface CatalogFacade {

    Promise<Offer> getOffer(String offerId, ApiContext apiContext);

    Promise<List<ItemId>> getItemsInOffer(String offerId);

    Promise<ItemRevision> getAppItemRevision(ItemId itemId, Integer versionCode, ApiContext apiContext);

    Promise<OfferAttribute> getOfferCategoryByName(String name, LocaleId locale);

    Promise<OfferAttribute> getOfferAttribute(String attributeId, ApiContext apiContext);

    Promise<com.junbo.catalog.spec.model.item.Item> getCatalogItemByPackageName(String packageName, Integer versionCode, String signatureHash);

    Promise<List<RevisionNote>> getRevisionNotes(ItemId itemId, ApiContext apiContext);

    Promise<Boolean> checkHostItem(ItemId itemId, ItemId hostItemId);
}
