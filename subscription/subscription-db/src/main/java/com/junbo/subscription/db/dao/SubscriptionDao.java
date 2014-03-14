/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.subscription.db.dao;

import com.junbo.subscription.db.entity.SubscriptionEntity;
import org.hibernate.Query;

import java.util.UUID;

/**
 * subscription dao.
 */
public class SubscriptionDao extends BaseDao<SubscriptionEntity> {

    public SubscriptionEntity getByTrackingUuid(UUID trackingUuid) {
        String queryString = "from Subscription where trackingUuid = (:trackingUuid)";
        Query q = currentSession().createQuery(queryString).setParameter("trackingUuid", trackingUuid);
        return (SubscriptionEntity) q.uniqueResult();
    }

}
