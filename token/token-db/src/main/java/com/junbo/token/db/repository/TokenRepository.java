/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repository;

import com.junbo.token.db.dao.*;
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

    public void addTokenSet(){

    }
}
