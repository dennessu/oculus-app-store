/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */


package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenSetOfferEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Token Set Offer Dao.
 */
public class TokenSetOfferDao extends GenericDAOImpl<TokenSetOfferEntity, Long> {
    public TokenSetOfferDao() {
        super(TokenSetOfferEntity.class);
    }

    public List<TokenSetOfferEntity> getByTokenSetId(final Long tokenSetId) {
        Criteria criteria = currentSession().createCriteria(TokenSetOfferEntity.class);
        criteria.add(Restrictions.eq("tokenSetId", tokenSetId));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}
