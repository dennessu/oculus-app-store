/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.balance.BalanceEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by xmchen on 14-1-21.
 */
public interface BalanceEntityDao extends BaseDao<BalanceEntity, Long> {
    List<BalanceEntity> getByTrackingUuid(UUID trackingUuid);
}
