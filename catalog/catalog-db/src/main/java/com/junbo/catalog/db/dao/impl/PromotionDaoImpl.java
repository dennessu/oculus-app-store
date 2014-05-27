/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.PromotionDao;
import com.junbo.catalog.db.entity.PromotionEntity;
import com.junbo.catalog.spec.model.promotion.PromotionsGetOptions;
import org.hibernate.Criteria;

import java.util.List;

/**
 * Promotion DAO implementation.
 */
public class PromotionDaoImpl extends BaseDaoImpl<PromotionEntity> implements PromotionDao {
    @Override
    public List<PromotionEntity> getPromotions(final PromotionsGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                addIdRestriction("promotionId", options.getPromotionIds(), criteria);
                criteria.setMaxResults(options.getValidSize()).setFirstResult(options.getValidStart());
            }
        });
    }
}
