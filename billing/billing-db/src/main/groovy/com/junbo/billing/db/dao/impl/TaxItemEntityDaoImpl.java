/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.balance.TaxItemEntity;
import com.junbo.billing.db.dao.TaxItemEntityDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class TaxItemEntityDaoImpl extends BaseDaoImpl<TaxItemEntity, Long>
        implements TaxItemEntityDao {
    public List<TaxItemEntity> findByBalanceItemId(Long balanceItemId) {
        Criteria criteria = currentSession().createCriteria(TaxItemEntity.class).
                add(Restrictions.eq("balanceItemId", balanceItemId));
        return criteria.list();

    }
}
