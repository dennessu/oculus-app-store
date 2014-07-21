/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.cloudant;

import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;
import com.junbo.token.db.repo.TokenSetRepository;
import com.junbo.token.spec.internal.TokenSet;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenSetRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<TokenSet, String> implements TokenSetRepository{
}
