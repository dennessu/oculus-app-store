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
import com.junbo.common.model.Results;
import com.junbo.langur.core.promise.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.ws.rs.core.Response;
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
    public Promise<Results<ShippingAddress>> getShippingAddresses(UserId userId) {
        return shippingAddressService.getShippingAddresses(userId.getValue())
                .then(new Promise.Func<List<ShippingAddress>, Promise<Results<ShippingAddress>>>() {
            @Override
            public Promise<Results<ShippingAddress>> apply(List<ShippingAddress> shippingAddresses) {
                Results<ShippingAddress> results = new Results<>();
                results.setItems(shippingAddresses);
                return Promise.pure(results);
            }
        });
    }

    @Override
    public Promise<ShippingAddress> getShippingAddress(UserId userId, ShippingAddressId addressId) {
        return shippingAddressService.getShippingAddress(userId.getValue(), addressId.getValue());
    }

    @Override
    public Promise<Response> deleteShippingAddress(UserId userId, ShippingAddressId addressId) {
        shippingAddressService.deleteShippingAddress(userId.getValue(), addressId.getValue());
        return Promise.pure(Response.status(204).build());
    }
}
