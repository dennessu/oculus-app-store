/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao.impl;

import com.junbo.fulfilment.common.util.Action;
import com.junbo.fulfilment.db.dao.FulfilmentRequestDao;
import com.junbo.fulfilment.db.entity.FulfilmentRequestEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.UUID;

/**
 * FulfilmentRequestDaoImpl.
 */
public class FulfilmentRequestDaoImpl extends BaseDaoImpl<FulfilmentRequestEntity> implements FulfilmentRequestDao {
    public FulfilmentRequestEntity findByTrackingUuid(Long userId, final String trackingUuid) {
        return findBy(userId, new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("trackingUuid", UUID.fromString(trackingUuid)));
            }
        });
    }

    @Override
    public FulfilmentRequestEntity findByOrderId(final Long billingOrderId) {
        return findBy(billingOrderId, new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("orderId", billingOrderId));
            }
        });
    }
}
