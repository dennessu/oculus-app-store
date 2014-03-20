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
import org.hibernate.criterion.Restrictions;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Offer draft DAO implementation.
 */
public class OfferDraftDaoImpl extends VersionedDaoImpl<OfferDraftEntity> implements OfferDraftDao {
    @Override
    public List<OfferDraftEntity> getOffers(final int start, final int size, final String status) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                criteria.setFirstResult(start);
                criteria.setFetchSize(size);
                if (!StringUtils.isEmpty(status)) {
                    criteria.add(Restrictions.eq("status", status));
                }
            }
        });
    }
}
