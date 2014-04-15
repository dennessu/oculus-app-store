/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.OfferRevisionDao;
import com.junbo.catalog.db.entity.OfferRevisionEntity;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Offer revision DAO implementation.
 */
public class OfferRevisionDaoImpl extends BaseDaoImpl<OfferRevisionEntity> implements OfferRevisionDao {
    @Override
    public List<OfferRevisionEntity> getRevisions(final OfferRevisionsGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                addIdRestriction("offerId", options.getOfferIds(), criteria);
                addIdRestriction("revisionId", options.getRevisionIds(), criteria);
                if (!StringUtils.isEmpty(options.getStatus())) {
                    criteria.add(Restrictions.eq("status", options.getStatus()));
                }
                options.ensurePagingValid();
                criteria.setFirstResult(options.getStart()).setMaxResults(options.getSize());
            }
        });
    }

    public OfferRevisionEntity getRevision(final Long offerId, final Long timestamp) {
        return findBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("offerId", offerId));
                criteria.add(Restrictions.le("timestamp", timestamp));
                criteria.addOrder(Order.desc("timestamp"));
                criteria.setMaxResults(1);
            }
        });
    }
}
