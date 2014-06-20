/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.dao;

import com.junbo.sharding.view.ViewQuery;
import com.junbo.subscription.db.entity.SubscriptionEventType;
import com.junbo.subscription.db.entity.SubscriptionOperationEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-6-20.
 */
public class SubscriptionOperationDao  extends BaseDao<SubscriptionOperationEntity>  {

    public List<SubscriptionOperationEntity> getPendingCycleOperation() {

        SubscriptionOperationEntity typeEntity = new SubscriptionOperationEntity();
        typeEntity.setEventTypeId(SubscriptionEventType.CYCLED);

        ViewQuery<Long> viewQuery = viewQueryFactory.from(typeEntity);
        if (viewQuery != null) {
            List<Long> subsOperationId = viewQuery.list();

            List<SubscriptionOperationEntity> operationEntities = new ArrayList<>();
            for (Long id : subsOperationId) {
                SubscriptionOperationEntity entity =  get(id);
                if (entity != null) {
                    operationEntities.add(entity);
                }
            }
            return operationEntities;
        }

        return null;
    }

}
