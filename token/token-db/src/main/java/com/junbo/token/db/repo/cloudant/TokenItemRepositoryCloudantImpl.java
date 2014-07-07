/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.cloudant;

import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;
import com.junbo.token.db.repo.TokenItemRepository;
import com.junbo.token.spec.model.TokenItem;

import java.util.List;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenItemRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<TokenItem, String> implements TokenItemRepository{
    @Override
    public Promise<TokenItem> getByHashValue(Long hashValue) {
        return super.queryView("by_hash_value", hashValue.toString()).
                then(new Promise.Func<List<TokenItem>, Promise<TokenItem>>() {
            @Override
            public Promise<TokenItem> apply(List<TokenItem> tokenItems) {
                if(tokenItems != null && tokenItems.size() > 0){
                    return Promise.pure(tokenItems.get(0));
                }else{
                    return Promise.pure(null);
                }
            }
        });
    }
}
