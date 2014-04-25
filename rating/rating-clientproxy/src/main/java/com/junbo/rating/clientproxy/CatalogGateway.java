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
import java.util.Map;
import java.util.Set;

/**
 * Catalog gateway interface.
 */
public interface CatalogGateway {
    Item getItem(Long itemId);
    RatingOffer getOffer(Long offerId, String timestamp);
    List<PromotionRevision> getPromotions();
    ShippingMethod getShippingMethod(Long shippingMethodId);
    Map<Long, String> getEntitlementDefinitions(Set<String> groups);
}
