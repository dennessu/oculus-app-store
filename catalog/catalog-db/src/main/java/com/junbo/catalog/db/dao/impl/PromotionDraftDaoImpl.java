/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.PromotionDraftDao;
import com.junbo.catalog.db.entity.PromotionDraftEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Promotion draft DAO implementation.
 */
public class PromotionDraftDaoImpl extends VersionedDaoImpl<PromotionDraftEntity> implements PromotionDraftDao {
    @Override
    public List<PromotionDraftEntity> getEffectivePromotions(final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                criteria.add(Restrictions.le("startDate", Utils.now()));
                criteria.add(Restrictions.ge("endDate", Utils.now()));
            }
        });
    }
}
