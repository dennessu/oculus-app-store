/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenConsumptionEntity;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by Administrator on 14-3-18.
 */
public class TokenConsumptionDao extends CommonDataDAOImpl<TokenConsumptionEntity, String> {
    public TokenConsumptionDao() {
        super(TokenConsumptionEntity.class);
    }

    public List<TokenConsumptionEntity> getByTokenItemId(final String itemID) {
        Criteria criteria = currentSession(itemID).createCriteria(TokenConsumptionEntity.class);
        criteria.add(Restrictions.eq("itemId", itemID));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return criteria.list();
    }
}
