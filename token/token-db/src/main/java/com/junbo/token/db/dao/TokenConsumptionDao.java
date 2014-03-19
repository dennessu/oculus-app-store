/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenConsumptionEntity;

/**
 * Created by Administrator on 14-3-18.
 */
public class TokenConsumptionDao extends CommonDataDAOImpl<TokenConsumptionEntity, Long> {
    public TokenConsumptionDao() {
        super(TokenConsumptionEntity.class);
    }
}
