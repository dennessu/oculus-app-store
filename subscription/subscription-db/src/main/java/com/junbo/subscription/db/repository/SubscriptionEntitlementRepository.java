/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.repository;

import com.junbo.subscription.db.dao.SubscriptionEntitlementDao;
import com.junbo.subscription.db.entity.SubscriptionEntitlementEntiy;
import com.junbo.subscription.db.mapper.SubscriptionMapper;
import com.junbo.subscription.spec.model.SubscriptionEntitlement;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 14-4-4.
 */
public class SubscriptionEntitlementRepository {
    @Autowired
    private SubscriptionEntitlementDao subscriptionEntitlementDao;
    @Autowired
    private SubscriptionMapper subscriptionMapper;

    public SubscriptionEntitlement insert(SubscriptionEntitlement entitlement) {
        Long id = subscriptionEntitlementDao.insert(subscriptionMapper.toSubsEntitlementEntity(entitlement));
        SubscriptionEntitlementEntiy result = subscriptionEntitlementDao.get(id);
        return subscriptionMapper.toSubsEntitlement(result);

    }
}
