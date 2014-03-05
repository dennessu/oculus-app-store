/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.balance.BalanceItemEntity;
import com.junbo.billing.db.dao.BalanceItemEntityDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class BalanceItemEntityDaoImpl extends BaseDaoImpl<BalanceItemEntity, Long>
        implements BalanceItemEntityDao {

    public List<BalanceItemEntity> findByBalanceId(Long balanceId) {
        Criteria criteria = currentSession().createCriteria(BalanceItemEntity.class).
                add(Restrictions.eq("balanceId", balanceId));
        return criteria.list();
    }
}
