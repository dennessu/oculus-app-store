/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.balance.DiscountItemEntity;
import com.junbo.billing.db.dao.DiscountItemEntityDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by xmchen on 14-1-21.
 */
@SuppressWarnings("unchecked")
public class DiscountItemEntityDaoImpl extends BaseDaoImpl<DiscountItemEntity, Long>
        implements DiscountItemEntityDao {
    public List<DiscountItemEntity> findByBalanceItemId(Long balanceItemId) {
        Criteria criteria = currentSession().createCriteria(DiscountItemEntity.class).
                add(Restrictions.eq("balanceItemId", balanceItemId));
        return criteria.list();
    }
}
