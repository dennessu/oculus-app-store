/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repository;

import com.junbo.oom.core.MappingContext;
import com.junbo.token.db.dao.*;
import com.junbo.token.db.entity.TokenSetEntity;
import com.junbo.token.db.mapper.TokenMapper;
import com.junbo.token.spec.internal.TokenSet;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Token Repository.
 */
public class TokenRepository {
    @Autowired
    private TokenSetDao tokenSetDao;
    @Autowired
    private TokenOrderDao tokenOrderDao;
    @Autowired
    private TokenItemDao tokenItemDao;
    @Autowired
    private TokenSetOfferDao tokenSetOfferDao;
    @Autowired
    private TokenConsumptionDao tokenConsumptionDao;
    @Autowired
    private TokenMapper tokenMapper;

    public void addTokenSet(TokenSet tokenSet){
        TokenSetEntity entity = tokenMapper.toTokenSetEntity(tokenSet, new MappingContext());

    }

    public void getTokenSet(){

    }

    public void addTokenOrder(){

    }

    public void getTokenOrder(){

    }
}
