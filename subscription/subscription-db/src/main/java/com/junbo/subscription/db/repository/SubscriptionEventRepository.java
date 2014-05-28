/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.repository;

import com.junbo.subscription.db.dao.SubscriptionEventDao;
import com.junbo.subscription.db.entity.SubscriptionEventEntity;
import com.junbo.subscription.db.mapper.SubscriptionMapper;
import com.junbo.subscription.spec.model.SubscriptionEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 14-3-28.
 */
public class SubscriptionEventRepository {
    @Autowired
    private SubscriptionEventDao subscriptionEventDao;
    @Autowired
    private SubscriptionMapper subscriptionMapper;

    public SubscriptionEvent get(Long eventId) {
        SubscriptionEventEntity entity;
        try {
            entity = subscriptionEventDao.get(eventId);
        }
        catch (Exception e){
            return null;
        }
        return subscriptionMapper.toSubsEvent(entity);
    }

    public SubscriptionEvent insert(SubscriptionEvent event) {
        Long id = subscriptionEventDao.insert(subscriptionMapper.toSubsEventEntity(event));
        SubscriptionEventEntity result = subscriptionEventDao.get(id);
        return subscriptionMapper.toSubsEvent(result);

    }

}
