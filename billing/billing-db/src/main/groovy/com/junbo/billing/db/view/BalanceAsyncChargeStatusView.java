/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.view;

import com.junbo.billing.db.entity.BalanceEntity;
import com.junbo.sharding.view.EntityView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xmchen on 14-4-14.
 */
public class BalanceAsyncChargeStatusView implements EntityView<Long, BalanceEntity, String> {
    @Override
    public String getName() {
        return "balance_async_charge_status";
    }

    @Override
    public Class<Long> getIdType() {
        return Long.class;
    }

    @Override
    public Class<BalanceEntity> getEntityType() {
        return BalanceEntity.class;
    }

    @Override
    public Class<String> getKeyType() {
        return String.class;
    }

    @Override
    public boolean handlesEntity(BalanceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        return entity.getIsAsyncCharge() != null && entity.getStatusId() != null;
    }

    @Override
    public List<String> mapEntity(BalanceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }
        List<String> result = new ArrayList<>();
        result.add(entity.getIsAsyncCharge().toString() + ":" + entity.getStatusId());
        return result;
    }
}
