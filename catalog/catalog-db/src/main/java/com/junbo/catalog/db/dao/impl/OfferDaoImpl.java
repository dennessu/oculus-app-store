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
import com.junbo.common.id.OfferId;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
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
                if (!CollectionUtils.isEmpty(options.getOfferIds())) {
                    List<Long> offerIds = new ArrayList<>();
                    for (OfferId offerId : options.getOfferIds()) {
                        offerIds.add(offerId.getValue());
                    }
                    criteria.add(Restrictions.in("offerId", offerIds));
                } else {
                    options.ensurePagingValid();
                    if (options.getCurated() != null) {
                        criteria.add(Restrictions.eq("curated", options.getCurated()));
                    }
                    if (options.getCategory() != null) {
                        criteria.add(Restrictions.sqlRestriction(options.getCategory() + "=ANY(categories)"));
                    }
                    criteria.setFirstResult(options.getStart());
                    criteria.setMaxResults(options.getSize());
                    //criteria.setFetchSize(options.getSize());
                }
            }
        });
    }
}
