/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenSetEntity;

/**
 * token set dao.
 */
public class TokenSetDao extends GenericDAOImpl<TokenSetEntity, String> {
    public TokenSetDao() {
        super(TokenSetEntity.class);
    }
}
