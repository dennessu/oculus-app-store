/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.dao.paymentinstrument;

import com.junbo.payment.db.dao.CommonDataDAOImpl;
import com.junbo.payment.db.entity.paymentinstrument.FacebookPaymentAccountMappingEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Facebook Payment Account Dao.
 */
public class FacebookPaymentAccountDao  extends CommonDataDAOImpl<FacebookPaymentAccountMappingEntity, Long> {
    public FacebookPaymentAccountDao() {
        super(FacebookPaymentAccountMappingEntity.class);
    }

    public List<FacebookPaymentAccountMappingEntity> getByUserId(final Long userId) {
        Criteria criteria = currentSession(userId).createCriteria(FacebookPaymentAccountMappingEntity.class);
        criteria.add(Restrictions.eq("userId", userId));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}
