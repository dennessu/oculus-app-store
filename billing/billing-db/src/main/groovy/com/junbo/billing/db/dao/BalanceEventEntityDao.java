/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.entity.BalanceEventEntity;

/**
 * Created by xmchen on 14-1-21.
 */
public interface BalanceEventEntityDao {
    BalanceEventEntity get(Long balanceEventId);
    BalanceEventEntity save(BalanceEventEntity balanceEvent);
    BalanceEventEntity update(BalanceEventEntity balanceEvent, BalanceEventEntity oldBalanceEvent);
}
