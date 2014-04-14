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

        taxItem.setTaxItemId(idGenerator.nextId(taxItem.getBalanceItemId()));

        Session session = currentSession(taxItem.getTaxItemId());
        session.save(taxItem);
        session.flush();
        return get(taxItem.getTaxItemId());
    }

    @Override
    public TaxItemEntity update(TaxItemEntity taxItem) {

        Session session = currentSession(taxItem.getTaxItemId());
        session.merge(taxItem);
        session.flush();

        return get(taxItem.getTaxItemId());
    }

    public List<TaxItemEntity> findByBalanceItemId(Long balanceItemId) {
        Criteria criteria = currentSession(balanceItemId).createCriteria(TaxItemEntity.class).
                add(Restrictions.eq("balanceItemId", balanceItemId));
        return criteria.list();

    }
}
