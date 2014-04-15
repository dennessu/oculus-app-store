/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.ShippingAddressEntity;

import java.util.List;

/**
 * Created by xmchen on 14-1-20.
 */
public interface ShippingAddressEntityDao {

    ShippingAddressEntity get(Long shippingAddressId);

    ShippingAddressEntity save(ShippingAddressEntity shippingAddress);

    ShippingAddressEntity update(ShippingAddressEntity shippingAddress);

    List<ShippingAddressEntity> findByUserId(Long userId);

    void softDelete(Long addressId);
}
