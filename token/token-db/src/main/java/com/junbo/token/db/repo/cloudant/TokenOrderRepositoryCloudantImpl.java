/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.token.db.repo.cloudant;

import com.junbo.sharding.repo.BaseCloudantRepositoryForDualWrite;
import com.junbo.token.db.repo.TokenOrderRepository;
import com.junbo.token.spec.internal.TokenOrder;

/**
 * Created by Administrator on 14-7-3.
 */
public class TokenOrderRepositoryCloudantImpl extends BaseCloudantRepositoryForDualWrite<TokenOrder, String> implements TokenOrderRepository {
}
