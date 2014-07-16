/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.BalanceItemEventEntity;

/**
 * Created by xmchen on 14-4-17.
 */
public interface BalanceItemEventEntityDao {
    BalanceItemEventEntity get(Long balanceItemEventId);
    BalanceItemEventEntity save(BalanceItemEventEntity balanceItemEvent);
    BalanceItemEventEntity update(BalanceItemEventEntity balanceItemEvent, BalanceItemEventEntity oldBalanceItemEvent);
}
