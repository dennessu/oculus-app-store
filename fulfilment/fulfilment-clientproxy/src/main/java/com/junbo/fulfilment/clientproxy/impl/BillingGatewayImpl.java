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
import com.junbo.fulfilment.common.util.Utils;
import com.junbo.fulfilment.spec.error.AppErrors;
import com.junbo.fulfilment.spec.fusion.ShippingAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * BillingGatewayImpl.
 */
public class BillingGatewayImpl implements BillingGateway {
    private static final UserId BLIND_USERID_MAPPING = null;
    private static final ShippingAddressId BLIND_ADDRESSID_MAPPING = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(BillingGatewayImpl.class);

    @Autowired
    @Qualifier("shippingAddressClient")
    private ShippingAddressResource shippingAddressResource;

    @Override
    public ShippingAddress getShippingAddress(Long userId, Long shippingAddressId) {
        try {
            com.junbo.billing.spec.model.ShippingAddress retrieved =
                    shippingAddressResource.getShippingAddress(new UserId(userId),
                            new ShippingAddressId(shippingAddressId)).wrapped().get();

            if (retrieved == null) {
                LOGGER.error("ShippingAddress [" + shippingAddressId + "] does not exist");
                throw AppErrors.INSTANCE.notFound("ShippingAddress", shippingAddressId).exception();
            }

            return wash(retrieved);
        } catch (Exception e) {
            LOGGER.error("Error occurred during calling [Billing] component.", e);
            throw AppErrors.INSTANCE.gatewayFailure("billing").exception();
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
