/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.rest.resource;

import com.junbo.billing.core.service.ShippingAddressService;
import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.billing.spec.resource.ShippingAddressResource;
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
    public Promise<ShippingAddress> postShippingAddress(ShippingAddress address) {
        return shippingAddressService.addShippingAddress(address);
    }

    @Override
    public Promise<List<ShippingAddress>> getShippingAddresses(Long userId) {
        return shippingAddressService.getShippingAddresses(userId);
    }

    @Override
    public Promise<ShippingAddress> getShippingAddress(Long addressId) {
        return shippingAddressService.getShippingAddress(addressId);
    }

    @Override
    public Promise<Void> deleteShippingAddress(Long addressId) {
        shippingAddressService.deleteShippingAddress(addressId);
        return null;
    }
}
