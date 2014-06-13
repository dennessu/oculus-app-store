/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy;

import com.junbo.fulfilment.spec.fusion.Item;
import com.junbo.fulfilment.spec.fusion.Offer;
import com.junbo.fulfilment.spec.fusion.ShippingMethod;

/**
 * CatalogGateway.
 */
public interface CatalogGateway {
    Offer getOffer(String offerId, Long timestamp);

    Item getItem(String itemId, Long timestamp);

    ShippingMethod getShippingMethod(String shippingMethodId);
}
