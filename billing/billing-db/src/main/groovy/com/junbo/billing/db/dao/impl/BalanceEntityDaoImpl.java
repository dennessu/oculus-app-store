/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;


import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.dao.BalanceEntityDao;
import com.junbo.billing.db.entity.BalanceEntity;
import com.junbo.billing.spec.enums.BalanceStatus;
import com.junbo.billing.spec.enums.BalanceType;
import com.junbo.sharding.view.ViewQuery;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.*;

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
        if (balance.getId() == null) {
            balance.setId(idGenerator.nextId(balance.getUserId()));
        }
        balance.setCreatedTime(new Date());
        if (balance.getCreatedBy() == null) {
            balance.setCreatedBy("0");
        }
        Session session = currentSession(balance.getId());
        session.save(balance);
        session.flush();
        return get(balance.getId());
    }

    @Override
    public BalanceEntity update(BalanceEntity balance, BalanceEntity oldBalance) {
        balance.setUpdatedTime(new Date());
        if (balance.getUpdatedBy() == null) {
            balance.setUpdatedBy("0");
        }

        Session session = currentSession(balance.getId());
        session.merge(balance);
        session.flush();
        return get(balance.getId());
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
    public List<BalanceEntity> getInitBalances() {
        BalanceEntity example = new BalanceEntity();
        example.setStatusId(BalanceStatus.INIT.getId());

        ViewQuery<Long> viewQuery = viewQueryFactory.from(example);
        if (viewQuery != null) {
            List<Long> balanceIds = viewQuery.list();

            List<BalanceEntity> balanceEntities = new ArrayList<>();
            for (Long id : balanceIds) {
                BalanceEntity entity =  get(id);
                if (entity != null) {
                    balanceEntities.add(entity);
                }
            }
            return balanceEntities;
        }

        return null;
    }

    @Override
    public List<BalanceEntity> getAwaitingPaymentBalances() {
        BalanceEntity example = new BalanceEntity();
        example.setStatusId(BalanceStatus.AWAITING_PAYMENT.getId());

        ViewQuery<Long> viewQuery = viewQueryFactory.from(example);
        if (viewQuery != null) {
            List<Long> balanceIds = viewQuery.list();

            List<BalanceEntity> balanceEntities = new ArrayList<>();
            for (Long id : balanceIds) {
                BalanceEntity entity =  get(id);
                if (entity != null) {
                    balanceEntities.add(entity);
                }
            }
            return balanceEntities;
        }

        return null;
    }

    @Override
    public List<BalanceEntity> getUnconfirmedBalances() {
        BalanceEntity example = new BalanceEntity();
        example.setStatusId(BalanceStatus.UNCONFIRMED.getId());

        ViewQuery<Long> viewQuery = viewQueryFactory.from(example);
        if (viewQuery != null) {
            List<Long> balanceIds = viewQuery.list();

            List<BalanceEntity> balanceEntities = new ArrayList<>();
            for (Long id : balanceIds) {
                BalanceEntity entity =  get(id);
                if (entity != null) {
                    balanceEntities.add(entity);
                }
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
