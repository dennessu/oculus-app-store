/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.clientproxy.impl;

import com.junbo.billing.spec.resource.ShippingAddressResource;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.id.UserId;
import com.junbo.fulfilment.clientproxy.BillingGateway;
import com.junbo.fulfilment.common.exception.CatalogGatewayException;
import com.junbo.fulfilment.common.exception.ResourceNotFoundException;
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.fusion.ShippingAddress;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * BillingGatewayImpl.
 */
public class BillingGatewayImpl implements BillingGateway {
    private static final UserId BLIND_USERID_MAPPING = null;
    private static final ShippingAddressId BLIND_ADDRESSID_MAPPING = null;

    @Autowired
    private ShippingAddressResource shippingAddressResource;

    @Override
    public ShippingAddress getShippingAddress(Long userId, Long shippingAddressId) {
        try {
            com.junbo.billing.spec.model.ShippingAddress retrieved =
                    shippingAddressResource.getShippingAddress(new UserId(userId),
                            new ShippingAddressId(shippingAddressId)).wrapped().get();

            if (retrieved == null) {
                throw new ResourceNotFoundException("ShippingAddress [" + shippingAddressId + "] does not exist");
            }

            return wash(retrieved);
        } catch (Exception e) {
            throw new CatalogGatewayException("Error occurred during calling [Billing] component service.", e);
        }
    }

    private ShippingAddress wash(com.junbo.billing.spec.model.ShippingAddress source) {
        Long userId = source.getUserId().getValue();
        Long addressId = source.getAddressId().getValue();
        source.setUserId(BLIND_USERID_MAPPING);
        source.setAddressId(BLIND_ADDRESSID_MAPPING);

        ShippingAddress result = Utils.map(source, ShippingAddress.class);
        result.setUserId(userId);
        result.setAddressId(addressId);

        return result;
    }
}
