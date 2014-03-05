/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.payment;

import com.junbo.payment.db.dao.CommonDataDAOImpl;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * payment event dao.
 */
public class PaymentEventDao extends CommonDataDAOImpl<PaymentEventEntity, Long> {
    public PaymentEventDao() {
        super(PaymentEventEntity.class);
    }

    public List<PaymentEventEntity> getByPaymentId(final Long paymentId) {
        Criteria criteria = currentSession().createCriteria(PaymentEventEntity.class);
        criteria.add(Restrictions.eq("paymentId", paymentId));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}
