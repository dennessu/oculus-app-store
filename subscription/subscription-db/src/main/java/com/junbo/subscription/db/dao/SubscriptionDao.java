/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.dao;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.UUID;

/**
 * subscription dao.
 */
public class SubscriptionDao extends BaseDao<SubscriptionEntity> {

    public List<SubscriptionEntity> getByTrackingUuid(Long userId, UUID trackingUuid) {
        Criteria criteria = currentSession(userId).createCriteria(SubscriptionEntity.class);
        criteria.add(Restrictions.eq("userId", userId));
        criteria.add(Restrictions.eq("trackingUuid", trackingUuid));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

}
