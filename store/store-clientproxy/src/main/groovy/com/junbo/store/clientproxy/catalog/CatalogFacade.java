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
import com.junbo.store.spec.model.browse.document.Item;
import com.junbo.store.spec.model.catalog.Offer;

/**
 * The CatalogFacade class.
 */
public interface CatalogFacade {

    Promise<Item> getItem(ItemId itemId, ApiContext apiContext);

    Promise<Offer> getOffer(String offerId, LocaleId locale);

    Promise<ItemRevision> getAppItemRevision(ItemId itemId, Integer versionCode);

    Promise<OfferAttribute> getOfferCategoryByName(String name, LocaleId locale);

    Promise<OfferAttribute> getOfferAttribute(String attributeId, ApiContext apiContext);
}
