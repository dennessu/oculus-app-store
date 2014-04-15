/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.PromotionRevisionDao;
import com.junbo.catalog.db.entity.PromotionRevisionEntity;
import com.junbo.catalog.spec.model.promotion.PromotionRevisionsGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Promotion revision DAO implementation.
 */
public class PromotionRevisionDaoImpl extends BaseDaoImpl<PromotionRevisionEntity> implements PromotionRevisionDao {
    @Override
    public List<PromotionRevisionEntity> getRevisions(final PromotionRevisionsGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                addIdRestriction("promotionId", options.getPromotionIds(), criteria);
                addIdRestriction("revisionId", options.getRevisionIds(), criteria);
                if (!StringUtils.isEmpty(options.getStatus())) {
                    criteria.add(Restrictions.eq("status", options.getStatus()));
                }
                options.ensurePagingValid();
                criteria.setFirstResult(options.getStart()).setMaxResults(options.getSize());
            }
        });
    }
}
