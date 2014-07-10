/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.dao.impl;

import com.junbo.order.db.dao.SubledgerDao;
import com.junbo.order.db.entity.SubledgerEntity;
import com.junbo.order.spec.model.enums.PayoutStatus;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by linyi on 14-2-8.
 */
@Repository("subledgerDao")
public class SubledgerDaoImpl extends BaseDaoImpl<SubledgerEntity> implements SubledgerDao {

    @Override
    public List<SubledgerEntity> getBySellerId(long sellerId, PayoutStatus payoutStatus,
                                               Date fromDate, Date toDate, int start, int count) {
        Criteria criteria = this.getSession(sellerId).createCriteria(SubledgerEntity.class);

        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.add(Restrictions.eq("payoutStatus", payoutStatus));

        if (fromDate != null) {
            criteria.add(Restrictions.ge("startTime", fromDate));
        }
        if (toDate != null) {
            criteria.add(Restrictions.lt("startTime", toDate));
        }

        setupPagingAndOrder(criteria, start, count);
        return criteria.list();
    }

    @Override
    public SubledgerEntity find(long sellerId, PayoutStatus payoutStatus, Date startTime,
                                String offerId, String currency, String country) {
        Criteria criteria = this.getSession(sellerId).createCriteria(SubledgerEntity.class);

        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.add(Restrictions.eq("payoutStatus", payoutStatus));
        criteria.add(Restrictions.eq("startTime", startTime));
        criteria.add(Restrictions.eq("offerId", offerId));
        criteria.add(Restrictions.eq("country", country));
        criteria.add(Restrictions.eq("currency", currency));

        criteria.setFirstResult(0);
        criteria.setMaxResults(1);

        List<SubledgerEntity> result =  criteria.list();
        return result.isEmpty() ? null : result.get(0);
    }

    private void setupPagingAndOrder(Criteria criteria, int start, int count) {
        criteria.setFirstResult(start);
        criteria.setMaxResults(count);
        criteria.addOrder(Order.desc("subledgerId"));
    }
}
