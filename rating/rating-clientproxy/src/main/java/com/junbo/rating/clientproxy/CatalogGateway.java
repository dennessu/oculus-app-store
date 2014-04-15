/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy;

import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.promotion.PromotionRevision;
import com.junbo.rating.spec.fusion.RatingOffer;

import java.util.List;

/**
 * Catalog gateway interface.
 */
public interface CatalogGateway {
    Item getItem(Long itemId);
    RatingOffer getOffer(Long offerId);
    List<PromotionRevision> getPromotions();
    ShippingMethod getShippingMethod(Long shippingMethodId);
}
