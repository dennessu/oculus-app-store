/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.cloudant;

import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;
import com.junbo.token.db.repo.TokenSetOfferRepository;
import com.junbo.token.spec.internal.TokenSetOffer;

import java.util.List;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenSetOfferRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<TokenSetOffer, String> implements TokenSetOfferRepository{
    @Override
    public Promise<List<TokenSetOffer>> getByTokenSetId(String tokenSetId) {
        return super.queryView("by_token_set_id", tokenSetId.toString());
    }
}
