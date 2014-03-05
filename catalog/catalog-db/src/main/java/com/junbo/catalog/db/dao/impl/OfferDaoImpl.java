/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.OfferDao;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.common.Status;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 * Offer DAO implementation.
 */
public class OfferDaoImpl extends BaseDaoImpl<OfferEntity> implements OfferDao {
    @Override
    public OfferEntity getOffer(final Long offerId, final Long timestamp) {
        OfferEntity offerEntity = findBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.add(Restrictions.eq("offerId", offerId));
                if (timestamp != null) {
                    criteria.add(Restrictions.le("timestamp", timestamp));
                }
                criteria.addOrder(Order.desc("timestamp"));
            }
        });

        if (Status.DELETED.equals(offerEntity.getStatus())) {
            offerEntity = null;
        }

        return offerEntity;
    }
}
