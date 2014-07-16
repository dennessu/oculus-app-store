/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.DiscountItemEntity;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
public interface DiscountItemEntityDao {
    DiscountItemEntity get(Long discountItemId);
    DiscountItemEntity save(DiscountItemEntity discountItem);
    DiscountItemEntity update(DiscountItemEntity discountItem, DiscountItemEntity oldDiscountItem);
    void softDelete(Long discountItemId);
    List<DiscountItemEntity> findByBalanceItemId(Long balanceItemId);
}
