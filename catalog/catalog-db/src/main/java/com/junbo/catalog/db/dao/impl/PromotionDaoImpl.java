/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.PromotionDao;
import com.junbo.catalog.db.entity.PromotionEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

/**
 * Promotion DAO implementation.
 */
public class PromotionDaoImpl extends BaseDaoImpl<PromotionEntity> implements PromotionDao {
    @Override
    public PromotionEntity getPromotion(final long promotionId, final int revision) {
        return findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("promotion_id", promotionId));
                criteria.add(Restrictions.eq("revision", revision));
            }
        });
    }

    /*@Override
    public List<PromotionEntity> getPromotions(final long promotionId, final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                criteria.add(Restrictions.eq("promotion_id", promotionId));
            }
        });
    }*/
}
