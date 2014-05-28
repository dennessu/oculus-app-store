/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.repository;

import com.junbo.subscription.db.dao.SubscriptionEventActionDao;
import com.junbo.subscription.db.entity.SubscriptionEventActionEntity;
import com.junbo.subscription.db.mapper.SubscriptionMapper;
import com.junbo.subscription.spec.model.SubscriptionEventAction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 14-3-28.
 */
public class SubscriptionEventActionRepository {
    @Autowired
    private SubscriptionEventActionDao subscriptionEventActionDao;

    @Autowired
    private SubscriptionMapper subscriptionMapper;

    public SubscriptionEventAction get(Long actionId) {
        SubscriptionEventActionEntity entity;
        try {
            entity = subscriptionEventActionDao.get(actionId);
        }
        catch (Exception e){
            return null;
        }
        return subscriptionMapper.toSubsEventAction(entity);
    }

    public SubscriptionEventAction insert(SubscriptionEventAction action) {
        Long id = subscriptionEventActionDao.insert(subscriptionMapper.toSubsEventActionEntity(action));
        SubscriptionEventActionEntity result = subscriptionEventActionDao.get(id);
        return subscriptionMapper.toSubsEventAction(result);

    }

}
