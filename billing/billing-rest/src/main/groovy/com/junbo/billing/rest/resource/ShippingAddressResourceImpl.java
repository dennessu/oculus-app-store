/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.rest.resource;

import com.junbo.billing.core.service.ShippingAddressService;
import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.billing.spec.resource.ShippingAddressResource;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.id.UserId;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
@Scope("prototype")
public class ShippingAddressResourceImpl implements ShippingAddressResource {

    @Autowired
    private ShippingAddressService shippingAddressService;

    @Override
    public Promise<ShippingAddress> postShippingAddress(UserId userId, ShippingAddress address) {
        return shippingAddressService.addShippingAddress(userId.getValue(), address);
    }

    @Override
    public Promise<List<ShippingAddress>> getShippingAddresses(UserId userId) {
        return shippingAddressService.getShippingAddresses(userId.getValue());
    }

    @Override
    public Promise<ShippingAddress> getShippingAddress(UserId userId, ShippingAddressId addressId) {
        return shippingAddressService.getShippingAddress(userId.getValue(), addressId.getValue());
    }

    @Override
    public Promise<Void> deleteShippingAddress(UserId userId, ShippingAddressId addressId) {
        shippingAddressService.deleteShippingAddress(userId.getValue(), addressId.getValue());
        return null;
    }
}
