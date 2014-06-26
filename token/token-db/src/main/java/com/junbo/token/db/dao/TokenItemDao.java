/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenItemEntity;

/**
 * token item dao.
 */
public class TokenItemDao extends CommonDataDAOImpl<TokenItemEntity, Long> {
    public TokenItemDao() {
        super(TokenItemEntity.class);
    }

    public TokenItemEntity getByHashValue(final Long hashValue) {
        //TODO: currently hashValue is the itemId PK
        return get(hashValue);
    }
}
