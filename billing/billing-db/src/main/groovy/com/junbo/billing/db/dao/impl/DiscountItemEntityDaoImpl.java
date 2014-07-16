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

import java.util.Date;
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
        if (discountItem.getId() == null) {
            discountItem.setId(idGenerator.nextId(discountItem.getBalanceItemId()));
        }
        discountItem.setCreatedTime(new Date());
        if (discountItem.getCreatedBy() == null) {
            discountItem.setCreatedBy("0");
        }

        Session session = currentSession(discountItem.getId());
        session.save(discountItem);
        session.flush();
        return get(discountItem.getId());
    }

    @Override
    public DiscountItemEntity update(DiscountItemEntity discountItem, DiscountItemEntity oldDiscountItem) {
        discountItem.setUpdatedTime(new Date());
        if (discountItem.getUpdatedBy() == null) {
            discountItem.setUpdatedBy("0");
        }
        Session session = currentSession(discountItem.getId());
        session.merge(discountItem);
        session.flush();

        return get(discountItem.getId());
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
        this.update(entity, entity);
    }
}
