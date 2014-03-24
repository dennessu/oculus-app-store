/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;


import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.balance.BalanceEntity;
import com.junbo.billing.db.dao.BalanceEntityDao;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.UUID;

/**
 * Created by xmchen on 14-1-21.
 */
public class BalanceEntityDaoImpl extends BaseDaoImpl<BalanceEntity, Long>
        implements BalanceEntityDao {
    @Override
    public List<BalanceEntity> getByTrackingUuid(UUID trackingUuid) {

        Criteria criteria = currentSession().createCriteria(BalanceEntity.class).
                add(Restrictions.eq("trackingUuid", trackingUuid));
        return criteria.list();
    }
}
