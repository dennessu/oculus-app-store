/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.fulfilment.db.dao.impl;

import com.junbo.fulfilment.common.util.Action;
import com.junbo.fulfilment.db.dao.FulfilmentDao;
import com.junbo.fulfilment.db.entity.FulfilmentEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * FulfilmentDaoImpl.
 */
public class FulfilmentDaoImpl extends BaseDaoImpl<FulfilmentEntity> implements FulfilmentDao {
    public List<FulfilmentEntity> findByRequestId(final Long requestId) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("requestId", requestId));
            }
        });
    }
}
