/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.TaxItemEntity;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
public interface TaxItemEntityDao {
    TaxItemEntity get(Long taxItemId);
    TaxItemEntity save(TaxItemEntity taxItem);
    TaxItemEntity update(TaxItemEntity taxItem);
    List<TaxItemEntity> findByBalanceItemId(Long balanceItemId);
}
