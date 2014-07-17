/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.BalanceItemEntity;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
public interface BalanceItemEntityDao {

    BalanceItemEntity get(Long balanceItemId);

    BalanceItemEntity save(BalanceItemEntity balanceItem);

    BalanceItemEntity update(BalanceItemEntity balanceItem, BalanceItemEntity oldBalanceItem);

    List<BalanceItemEntity> findByBalanceId(Long balanceId);

}
