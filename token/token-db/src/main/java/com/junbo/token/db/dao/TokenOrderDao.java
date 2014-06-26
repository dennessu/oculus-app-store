/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenOrderEntity;

/**
 * token order dao.
 */
public class TokenOrderDao extends GenericDAOImpl<TokenOrderEntity, Long> {
    public TokenOrderDao() {
        super(TokenOrderEntity.class);
    }
}
