/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo;

import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;
import com.junbo.token.spec.model.TokenItem;

/**
 * Created by Administrator on 14-7-3.
 */
public interface TokenItemRepository extends BaseRepository<TokenItem, String> {
    @ReadMethod
    Promise<TokenItem> getByHashValue(Long hashValue);
}
