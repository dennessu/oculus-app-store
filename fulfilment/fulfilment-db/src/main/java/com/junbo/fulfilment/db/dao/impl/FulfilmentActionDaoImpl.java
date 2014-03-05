/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao.impl;

import com.junbo.fulfilment.common.util.Action;
import com.junbo.fulfilment.db.dao.FulfilmentActionDao;
import com.junbo.fulfilment.db.entity.FulfilmentActionEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * FulfilmentActionDaoImpl.
 */
public class FulfilmentActionDaoImpl extends BaseDaoImpl<FulfilmentActionEntity> implements FulfilmentActionDao {
    @Override
    public List<FulfilmentActionEntity> findByFulfilmentId(final Long fulfilmentId) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("fulfilmentId", fulfilmentId));
            }
        });
    }
}
