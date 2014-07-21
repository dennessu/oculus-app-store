/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenItemEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * token item dao.
 */
public class TokenItemDao extends CommonDataDAOImpl<TokenItemEntity, String> {
    public TokenItemDao() {
        super(TokenItemEntity.class);
    }

    public TokenItemEntity getByHashValue(final Long hashValue) {
        Criteria criteria = currentSession(hashValue).createCriteria(TokenItemEntity.class);
        criteria.add(Restrictions.eq("hashValue", hashValue));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        List<TokenItemEntity> results = criteria.list();
        if(results == null || results.size() == 0){
            return  null;
        }
        return results.get(0);
    }
}
