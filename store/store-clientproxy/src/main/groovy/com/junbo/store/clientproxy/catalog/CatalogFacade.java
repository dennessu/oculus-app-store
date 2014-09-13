/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.clientproxy.catalog;

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

    Promise<Item> getItemData(ItemId itemId, ApiContext apiContext);

    Promise<Offer> getOffer(String offerId, LocaleId locale);

}
