/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */


package com.junbo.token.db.dao;

import com.junbo.token.db.entity.TokenSetOfferEntity;

/**
 * Token Set Offer Dao.
 */
public class TokenSetOfferDao extends CommonDataDAOImpl<TokenSetOfferEntity, Long> {
    public TokenSetOfferDao() {
        super(TokenSetOfferEntity.class);
    }
}
