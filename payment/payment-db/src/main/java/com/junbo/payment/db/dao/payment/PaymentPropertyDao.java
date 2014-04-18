/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.payment;

import com.junbo.payment.db.dao.CommonDataDAOImpl;
import com.junbo.payment.db.entity.payment.PaymentPropertyEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Payment Property Dao.
 */
public class PaymentPropertyDao extends CommonDataDAOImpl<PaymentPropertyEntity, Long> {
    public PaymentPropertyDao() {
        super(PaymentPropertyEntity.class);
    }

    public List<PaymentPropertyEntity> getByPaymentId(final Long paymentId) {
        Criteria criteria = currentSession(paymentId).createCriteria(PaymentPropertyEntity.class);
        criteria.add(Restrictions.eq("paymentId", paymentId));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}
