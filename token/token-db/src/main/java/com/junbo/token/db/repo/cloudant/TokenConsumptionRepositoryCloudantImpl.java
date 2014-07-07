/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.cloudant;

import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;
import com.junbo.token.db.repo.TokenConsumptionRepository;
import com.junbo.token.spec.model.TokenConsumption;

import java.util.List;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenConsumptionRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<TokenConsumption, String> implements TokenConsumptionRepository{
    @Override
    public Promise<List<TokenConsumption>> getByTokenItemId(String itemId) {
        return super.queryView("by_token_item_id", itemId.toString());
    }
}
