/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.OfferDraftDao;
import com.junbo.catalog.db.entity.OfferDraftEntity;
import org.hibernate.Criteria;

import java.util.List;

/**
 * Offer draft DAO implementation.
 */
public class OfferDraftDaoImpl extends VersionedDaoImpl<OfferDraftEntity> implements OfferDraftDao {
    @Override
    public List<OfferDraftEntity> getOffers(final int start, final int size) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                //criteria.add(Restrictions.eq("status", "PUBLISHED"));
            }
        });
    }
}
