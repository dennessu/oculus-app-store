/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao;


import com.junbo.payment.db.entity.TrackingUuidEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import java.util.List;
import java.util.UUID;


/**
 * trackingUuid dao.
 */
public class TrackingUuidDao extends CommonDataDAOImpl<TrackingUuidEntity, Long> {
    public TrackingUuidDao() {
        super(TrackingUuidEntity.class);
    }

    public List<TrackingUuidEntity> getByTrackingUuid(Long userId, UUID trackingUuid){
        Criteria criteria = currentSession(userId).createCriteria(TrackingUuidEntity.class);
        criteria.add(Restrictions.eq("userId", userId));
        criteria.add(Restrictions.eq("trackingUuid", trackingUuid));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}
