/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDao;
import com.junbo.billing.db.entity.TaxItemEntity;
import com.junbo.billing.db.dao.TaxItemEntityDao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class TaxItemEntityDaoImpl extends BaseDao implements TaxItemEntityDao {
    @Override
    public TaxItemEntity get(Long taxItemId) {
        return (TaxItemEntity)currentSession(taxItemId).get(TaxItemEntity.class, taxItemId);
    }

    @Override
    public TaxItemEntity save(TaxItemEntity taxItem) {
        if (taxItem.getId() == null) {
            taxItem.setId(idGenerator.nextId(taxItem.getBalanceItemId()));
        }
        taxItem.setCreatedTime(new Date());
        if (taxItem.getCreatedBy() == null) {
            taxItem.setCreatedBy("0");
        }
        Session session = currentSession(taxItem.getId());
        session.save(taxItem);
        session.flush();
        return get(taxItem.getId());
    }

    @Override
    public TaxItemEntity update(TaxItemEntity taxItem, TaxItemEntity oldTaxItem) {
        taxItem.setUpdatedTime(new Date());
        if (taxItem.getUpdatedBy() == null) {
            taxItem.setUpdatedBy("0");
        }
        Session session = currentSession(taxItem.getId());
        session.merge(taxItem);
        session.flush();

        return get(taxItem.getId());
    }

    public List<TaxItemEntity> findByBalanceItemId(Long balanceItemId) {
        Criteria criteria = currentSession(balanceItemId).createCriteria(TaxItemEntity.class);
        criteria.add(Restrictions.eq("balanceItemId", balanceItemId));
        criteria.add(Restrictions.eq("isDeleted", false));
        return criteria.list();

    }

    @Override
    public void softDelete(Long taxItem) {
        TaxItemEntity entity = this.get(taxItem);
        entity.setIsDeleted(Boolean.TRUE);
        this.update(entity, entity);
    }
}
