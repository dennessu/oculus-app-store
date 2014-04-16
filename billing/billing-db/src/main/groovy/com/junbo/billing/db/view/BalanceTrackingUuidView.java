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
import java.util.UUID;

/**
 * Created by xmchen on 14-4-14.
 */
public class BalanceTrackingUuidView implements EntityView<Long, BalanceEntity, UUID> {
    @Override
    public String getName() {
        return "balance_trackingUuid";
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
    public Class<UUID> getKeyType() {
        return UUID.class;
    }

    @Override
    public boolean handlesEntity(BalanceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        return entity.getTrackingUuid() != null;
    }

    @Override
    public List<UUID> mapEntity(BalanceEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        List<UUID> result = new ArrayList<>();
        result.add(entity.getTrackingUuid());
        return result;
    }
}
