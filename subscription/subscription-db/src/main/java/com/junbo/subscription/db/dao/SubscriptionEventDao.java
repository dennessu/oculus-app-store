/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.dao;

import com.junbo.sharding.view.ViewQuery;
import com.junbo.subscription.db.entity.SubscriptionEventEntity;
import com.junbo.subscription.db.entity.SubscriptionEventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-3-28.
 */
public class SubscriptionEventDao extends BaseDao<SubscriptionEventEntity> {

    public List<SubscriptionEventEntity> getPendingCycleEvent() {

        SubscriptionEventEntity typeEntity = new SubscriptionEventEntity();
        typeEntity.setEventTypeId(SubscriptionEventType.CYCLED);

        ViewQuery<Long> viewQuery = viewQueryFactory.from(typeEntity);
        if (viewQuery != null) {
            List<Long> subcriptionEventId = viewQuery.list();

            List<SubscriptionEventEntity> subscriptionEventEntities = new ArrayList<>();
            for (Long id : subcriptionEventId) {
                SubscriptionEventEntity entity =  get(id);
                if (entity != null) {
                    subscriptionEventEntities.add(entity);
                }
            }
            return subscriptionEventEntities;
        }

        return null;
    }

}
