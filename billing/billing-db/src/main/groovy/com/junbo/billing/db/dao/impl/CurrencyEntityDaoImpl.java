/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.dao.impl;

import com.junbo.billing.db.BaseDaoImpl;
import com.junbo.billing.db.currency.CurrencyEntity;
import com.junbo.billing.db.dao.CurrencyEntityDao;
import org.hibernate.Criteria;

import java.util.List;

/**
 * Created by xmchen on 14-2-13.
 */
@SuppressWarnings("unchecked")
public class CurrencyEntityDaoImpl extends BaseDaoImpl<CurrencyEntity, String>
        implements CurrencyEntityDao {

    public List<CurrencyEntity> loadAll() {
        Criteria criteria = currentSession().createCriteria(CurrencyEntity.class);
        criteria.setCacheable(true);
        return criteria.list();
    }

}
