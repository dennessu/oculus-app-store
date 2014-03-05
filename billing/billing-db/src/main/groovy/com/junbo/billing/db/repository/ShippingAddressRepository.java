/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.repository;

import com.junbo.billing.spec.model.ShippingAddress;

import java.util.List;

/**
 * Created by xmchen on 14-2-19.
 */
public interface ShippingAddressRepository {
    ShippingAddress saveShippingAddress(ShippingAddress address);

    List<ShippingAddress> getShippingAddresses(Long userId);

    ShippingAddress getShippingAddress(Long addressId);

    void deleteShippingAddress(Long addressId);
}
