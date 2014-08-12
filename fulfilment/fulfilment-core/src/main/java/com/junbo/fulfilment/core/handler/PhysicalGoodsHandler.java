/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.common.collection.Counter;
import com.junbo.fulfilment.core.context.PhysicalGoodsContext;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.model.FulfilmentAction;

/**
 * PhysicalGoodsHandler.
 */
public class PhysicalGoodsHandler extends HandlerSupport<PhysicalGoodsContext> {
    @Override
    public void process(PhysicalGoodsContext context) {
        //ShippingAddress shippingAddress = billingGateway.getShippingAddress(context.getUserId(), context.getShippingAddressId());
        //ShippingMethod shippingMethod = catalogGateway.getShippingMethod(context.getShippingMethodId());

        Counter counter = new Counter();
        for (FulfilmentAction action : context.getActions()) {
            for (LinkedEntry item : action.getItems()) {
                counter.count(getKey(item), action.getCopyCount() * item.getQuantity());
            }
        }

        context.setShipment(counter);
    }

    private String getKey(LinkedEntry item) {
        return item.getId() + "#" + item.getSku();
    }
}
