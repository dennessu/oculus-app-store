/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.address.ShippingAddressEntity;

import java.util.List;

/**
 * Created by xmchen on 14-1-20.
 */
public interface ShippingAddressEntityDao extends BaseDao<ShippingAddressEntity, Long> {
    List<ShippingAddressEntity> findByUserId(Long userId);

    void softDelete(Long addressId);
}
