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
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

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
        return innerList(sellerId, null, null, payoutStatus, fromDate, toDate, start, count, false);
    }

    @Override
    public List<SubledgerEntity> getByStatusOrderBySeller(int dataCenterId, int shardId, PayoutStatus payoutStatus, Date fromDate, Date toDate, int start, int count) {
        return innerList(null, dataCenterId, shardId, payoutStatus, fromDate, toDate, start, count, true);
    }

    @Override
    public List<SubledgerEntity> getByTime(int dataCenterId, int shardId, Date fromDate, Date toDate, int start, int count) {
        return innerList(null, dataCenterId, shardId, null, fromDate, toDate, start, count, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<SubledgerEntity> getByPayoutId(long payoutId, int start, int count) {
        Criteria criteria = this.getSession(payoutId).createCriteria(SubledgerEntity.class);
        criteria.add(Restrictions.eq("payoutId", payoutId));
        criteria.setFirstResult(start);
        criteria.setMaxResults(count);
        return criteria.list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public SubledgerEntity find(long sellerId, PayoutStatus payoutStatus, Date startTime,
                                String itemId, String subledgerKey, String currency, String country) {
        Criteria criteria = this.getSession(sellerId).createCriteria(SubledgerEntity.class);

        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.add(Restrictions.eq("payoutStatus", payoutStatus));
        criteria.add(Restrictions.eq("startTime", startTime));
        criteria.add(Restrictions.eq("itemId", itemId));
        criteria.add(Restrictions.eq("country", country));
        criteria.add(Restrictions.eq("currency", currency));
        criteria.add(Restrictions.eq("key", subledgerKey));

        criteria.setFirstResult(0);
        criteria.setMaxResults(1);

        List<SubledgerEntity> result =  criteria.list();
        return result.isEmpty() ? null : result.get(0);
    }

    private List<SubledgerEntity> innerList(Long sellerId, Integer dataCenterId, Integer shardId, PayoutStatus payoutStatus,
                                            Date fromDate, Date toDate, int start, int count, boolean orderBySellerId) {
        Session session;
        if (sellerId == null) {
            Assert.notNull(dataCenterId);
            Assert.notNull(shardId);
            session = this.getSessionByShardId(dataCenterId, shardId);
        } else {
            session = this.getSession(sellerId);
        }
        Criteria criteria = session.createCriteria(SubledgerEntity.class);

        if (sellerId != null) {
            criteria.add(Restrictions.eq("sellerId", sellerId));
        }
        if (payoutStatus != null) {
            criteria.add(Restrictions.eq("payoutStatus", payoutStatus));
        }

        if (fromDate != null) {
            criteria.add(Restrictions.ge("startTime", fromDate));
        }
        if (toDate != null) {
            criteria.add(Restrictions.lt("startTime", toDate));
        }

        setupPagingAndOrder(criteria, start, count, orderBySellerId);
        return criteria.list();
    }

    private void setupPagingAndOrder(Criteria criteria, int start, int count, boolean orderBySellerId) {
        criteria.setFirstResult(start);
        criteria.setMaxResults(count);
        if (orderBySellerId) {
            criteria.addOrder(Order.asc("sellerId"));
        } else {
            criteria.addOrder(Order.desc("subledgerId"));
        }
    }
}
