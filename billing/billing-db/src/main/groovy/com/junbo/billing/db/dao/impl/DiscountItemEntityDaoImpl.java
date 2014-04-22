/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.DiscountItemEntity;
import com.junbo.billing.db.dao.DiscountItemEntityDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class DiscountItemEntityDaoImpl extends BaseDao implements DiscountItemEntityDao {
    @Override
    public DiscountItemEntity get(Long discountItemId) {
        return (DiscountItemEntity)currentSession(discountItemId).get(DiscountItemEntity.class, discountItemId);
    }

    @Override
    public DiscountItemEntity save(DiscountItemEntity discountItem) {
        discountItem.setDiscountItemId(idGenerator.nextId(discountItem.getBalanceItemId()));

        Session session = currentSession(discountItem.getDiscountItemId());
        session.save(discountItem);
        session.flush();
        return get(discountItem.getDiscountItemId());
    }

    @Override
    public DiscountItemEntity update(DiscountItemEntity discountItem) {

        Session session = currentSession(discountItem.getDiscountItemId());
        session.merge(discountItem);
        session.flush();

        return get(discountItem.getDiscountItemId());
    }

    public List<DiscountItemEntity> findByBalanceItemId(Long balanceItemId) {
        Criteria criteria = currentSession(balanceItemId).createCriteria(DiscountItemEntity.class);
        criteria.add(Restrictions.eq("balanceItemId", balanceItemId));
        criteria.add(Restrictions.eq("isDeleted", false));
        return criteria.list();
    }

    @Override
    public void softDelete(Long discountItemId) {
        DiscountItemEntity entity = this.get(discountItemId);
        entity.setIsDeleted(Boolean.TRUE);
        this.update(entity);
    }
}
