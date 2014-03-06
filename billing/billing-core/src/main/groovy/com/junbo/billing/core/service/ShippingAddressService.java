/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.core.service;

import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.langur.core.promise.Promise;

import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
public interface ShippingAddressService {
    Promise<ShippingAddress> addShippingAddress(Long userId, ShippingAddress address);

    Promise<List<ShippingAddress>> getShippingAddresses(Long userId);

    Promise<ShippingAddress> getShippingAddress(Long userId, Long addressId);

    void deleteShippingAddress(Long userId, Long addressId);
}
