/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;


import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.BalanceEntity;
import com.junbo.billing.db.dao.BalanceEntityDao;
import com.junbo.billing.spec.enums.BalanceStatus;
import com.junbo.billing.spec.enums.BalanceType;
import com.junbo.sharding.view.ViewQuery;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by xmchen on 14-1-21.
 */
public class BalanceEntityDaoImpl extends BaseDao implements BalanceEntityDao {
    @Override
    public BalanceEntity get(Long balanceId) {
        return (BalanceEntity)currentSession(balanceId).get(BalanceEntity.class, balanceId);
    }

    @Override
    public BalanceEntity save(BalanceEntity balance) {

        balance.setBalanceId(idGenerator.nextId(balance.getUserId()));

        Session session = currentSession(balance.getBalanceId());
        session.save(balance);
        session.flush();
        return get(balance.getBalanceId());
    }

    @Override
    public BalanceEntity update(BalanceEntity balance) {

        Session session = currentSession(balance.getBalanceId());
        session.merge(balance);
        session.flush();

        return get(balance.getBalanceId());
    }

    @Override
    public List<BalanceEntity> getByTrackingUuid(UUID trackingUuid) {

        BalanceEntity example = new BalanceEntity();
        example.setTrackingUuid(trackingUuid);

        ViewQuery<Long> viewQuery = viewQueryFactory.from(example);
        if (viewQuery != null) {
            List<Long> balanceIds = viewQuery.list();

            List<BalanceEntity> balanceEntities = new ArrayList<>();
            for (Long id : balanceIds) {
                balanceEntities.add(get(id));
            }
            return balanceEntities;
        }

        return null;
    }

    @Override
    public List<BalanceEntity> getAsyncChargeInitBalances(Integer count) {
        BalanceEntity example = new BalanceEntity();
        example.setIsAsyncCharge(true);
        example.setStatusId(BalanceStatus.INIT.getId());

        ViewQuery<Long> viewQuery = viewQueryFactory.from(example);
        if (viewQuery != null) {
            List<Long> balanceIds = viewQuery.list();

            List<BalanceEntity> balanceEntities = new ArrayList<>();
            for (Long id : balanceIds) {
                balanceEntities.add(get(id));
            }
            return balanceEntities;
        }

        return null;
    }

    @Override
    public List<BalanceEntity> getRefundBalancesByOriginalId(Long balanceId) {
        Collection<Short> successStatus = new ArrayList<>();
        successStatus.add(BalanceStatus.COMPLETED.getId());
        successStatus.add(BalanceStatus.AWAITING_PAYMENT.getId());
        Criteria criteria = currentSession(balanceId).createCriteria(BalanceEntity.class)
                .add(Restrictions.eq("originalBalanceId", balanceId))
                .add(Restrictions.eq("typeId", BalanceType.REFUND.getId()))
                .add(Restrictions.in("statusId", successStatus));
        return criteria.list();
    }
}
