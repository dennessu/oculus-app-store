/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.Collection;
import java.util.List;

/**
 * Offer DAO implementation.
 */
public class OfferDaoImpl extends BaseDaoImpl<OfferEntity> implements OfferDao {
    @Override
    public List<OfferEntity> getOffers(final OffersGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                addIdRestriction("offerId", options.getOfferIds(), criteria);
                if (options.getPublished() != null) {
                    criteria.add(Restrictions.eq("published", options.getPublished()));
                }
                if (options.getCategory() != null) {
                    criteria.add(Restrictions.sqlRestriction(options.getCategory().getValue() + "=ANY(categories)"));
                }
                if (options.getOwnerId() != null) {
                    criteria.add(Restrictions.eq("ownerId", options.getOwnerId().getValue()));
                }
                criteria.setFirstResult(options.getValidStart()).setMaxResults(options.getValidSize());
            }
        });
    }

    @Override
    public List<OfferEntity> getOffers(final Collection<Long> offerIds) {
        return findAllBy(new Action<Criteria>() {
            @Override
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.in("offerId", offerIds));
            }
        });
    }
}
