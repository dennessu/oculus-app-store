/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.dao.PromotionDao;
import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * Promotion DAO implementation.
 */
public class PromotionDaoImpl extends BaseDaoImpl<PromotionEntity> implements PromotionDao {
    @Override
    public List<PromotionEntity> getEffectivePromotions(final PromotionsGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                addIdRestriction("promotionId", options.getPromotionIds(), criteria);
                Date now = Utils.now();
                criteria.add(Restrictions.and(Restrictions.lt("startDate", now), Restrictions.gt("endDate", now)));
                options.ensurePagingValid();
                criteria.setMaxResults(options.getSize()).setFirstResult(options.getStart());
            }
        });
    }
}
