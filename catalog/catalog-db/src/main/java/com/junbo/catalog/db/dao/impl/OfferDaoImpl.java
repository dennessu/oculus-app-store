/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.db.entity.OfferEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Offer DAO implementation.
 */
public class OfferDaoImpl extends BaseDaoImpl<OfferEntity> implements OfferDao {
    @Override
    public OfferEntity getOffer(final long offerId) {
        return findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("offerId", offerId));
                criteria.addOrder(Order.desc("revision"));
            }
        });
    }

    @Override
    public OfferEntity getOffer(final long offerId, final int revision) {
        return findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("offerId", offerId));
                criteria.add(Restrictions.eq("revision", revision));
            }
        });
    }

    @Override
    public List<Integer> getRevisions(long offerId) {
        Criteria criteria = currentSession().createCriteria(getEntityType());
        criteria.add(Restrictions.eq("offerId", offerId));
        criteria.setProjection(Projections.property("revision"));
        return (List<Integer>) criteria.list();
    }
}
