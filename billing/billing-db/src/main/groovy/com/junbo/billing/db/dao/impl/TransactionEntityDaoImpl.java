/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.dao.TransactionEntityDao;
import com.junbo.billing.db.transaction.TransactionEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class TransactionEntityDaoImpl extends BaseDaoImpl<TransactionEntity, Long>
        implements TransactionEntityDao {
    @Override
    public List<TransactionEntity> findByBalanceId(Long balanceId) {

        Criteria criteria = currentSession().createCriteria(TransactionEntity.class).
                add(Restrictions.eq("balanceId", balanceId));
        return criteria.list();
    }
}
