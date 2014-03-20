/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.rating.clientproxy;

import com.junbo.catalog.spec.model.attribute.Attribute;
import com.junbo.catalog.spec.model.domaindata.ShippingMethod;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.promotion.Promotion;
import com.junbo.rating.spec.fusion.RatingOffer;

import java.util.List;

/**
 * Catalog gateway interface.
 */
public interface CatalogGateway {
    Attribute getAttribute(Long attributeId);
    Item getItem(Long itemId);
    RatingOffer getOffer(Long offerId);
    List<Promotion> getPromotions();
    ShippingMethod getShippingMethod(Long shippingMethodId);
}
