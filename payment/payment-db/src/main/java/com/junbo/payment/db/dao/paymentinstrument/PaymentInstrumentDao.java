/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.paymentinstrument;

import com.junbo.payment.db.dao.CommonDataDAOImpl;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import java.util.List;

/**
 * payment instrument dao.
 */
public class PaymentInstrumentDao extends CommonDataDAOImpl<PaymentInstrumentEntity, Long> {
    public PaymentInstrumentDao() {
        super(PaymentInstrumentEntity.class);
    }

    public List<PaymentInstrumentEntity> getByUserId(final Long userId) {
        Criteria criteria = currentSession().createCriteria(PaymentInstrumentEntity.class);
        criteria.add(Restrictions.eq("userId", userId));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public void updateDefault(final Long userId, final Long piId){
        String query = "update " + PaymentInstrumentEntity.class.getSimpleName() +
                " set is_default = CASE WHEN payment_instrument_id = " + piId  +
                " THEN true ELSE false END" +
                " where user_id = " + userId;
        currentSession().createQuery(query).executeUpdate();
    }
}
