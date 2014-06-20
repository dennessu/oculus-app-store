/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.subscription.db.view;

import com.junbo.sharding.view.EntityView;
import com.junbo.subscription.db.entity.SubscriptionOperationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-6-16.
 */
public class SubsOperationView implements EntityView<Long, SubscriptionOperationEntity, String> {
    @Override
    public String getName() {
        return "subscription_event_status";
    }

    @Override
    public Class<Long> getIdType() {
        return Long.class;
    }

    @Override
    public Class<SubscriptionOperationEntity> getEntityType() {
        return SubscriptionOperationEntity.class;
    }

    @Override
    public Class<String> getKeyType() {
        return String.class;
    }

    @Override
    public boolean handlesEntity(SubscriptionOperationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }

        return entity.getOperationStatusId() != null;
    }

    @Override
    public List<String> mapEntity(SubscriptionOperationEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }
        List<String> result = new ArrayList<>();
        result.add(entity.getOperationStatusId().toString());
        return result;
    }
}
