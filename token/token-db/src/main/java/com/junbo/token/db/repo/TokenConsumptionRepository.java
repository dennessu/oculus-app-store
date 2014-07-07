/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;
import com.junbo.token.spec.model.TokenConsumption;

import java.util.List;

/**
 * Created by Administrator on 14-7-3.
 */
public interface TokenConsumptionRepository extends BaseRepository<TokenConsumption, String> {
    @ReadMethod
    Promise<List<TokenConsumption>> getByTokenItemId(String itemId);
}
