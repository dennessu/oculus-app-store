/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao.impl;

import com.junbo.catalog.common.util.Action;
import com.junbo.catalog.db.dao.OfferAttributeDao;
import com.junbo.catalog.db.entity.OfferAttributeEntity;
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Offer Attribute DAO implementation.
 */
public class OfferAttributeDaoImpl extends BaseDaoImpl<OfferAttributeEntity> implements OfferAttributeDao {
    public List<OfferAttributeEntity> getAttributes(final OfferAttributesGetOptions options) {
        return findAllBy(new Action<Criteria>() {
            public void apply(Criteria criteria) {
                addIdRestriction("id", options.getAttributeIds(), criteria);
                if (options.getAttributeType() != null) {
                    criteria.add(Restrictions.eq("type", options.getAttributeType()));
                }
                criteria.setFirstResult(options.getValidStart()).setMaxResults(options.getValidSize());
            }
        });
    }
}
