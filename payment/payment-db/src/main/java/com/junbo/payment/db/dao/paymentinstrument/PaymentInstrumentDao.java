/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.paymentinstrument;

import com.junbo.common.id.PIType;
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
        Criteria criteria = currentSession(userId).createCriteria(PaymentInstrumentEntity.class);
        criteria.add(Restrictions.eq("userId", userId));
        criteria.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    public List<PaymentInstrumentEntity> getByUserAndType(final Long userId, final PIType piType) {
        Criteria criteria = currentSession(userId).createCriteria(PaymentInstrumentEntity.class);
        criteria.add(Restrictions.eq("userId", userId));
        criteria.add(Restrictions.eq("isDeleted", Boolean.FALSE));
        if(piType != null){
            criteria.add(Restrictions.eq("type", piType.getId()));
        }
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }

    @Override
    public Long save(PaymentInstrumentEntity entity) {
        entity.setIsDeleted(false);
        return super.save(entity);
    }

    @Override
    public void delete(Long id) {
        PaymentInstrumentEntity entity = get(id);
        entity.setIsDeleted(true);
        update(entity);
    }
}
