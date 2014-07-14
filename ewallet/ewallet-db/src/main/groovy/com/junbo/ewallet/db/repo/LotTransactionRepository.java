/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.spec.model.LotTransaction;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;

/**
 * Created by minhao on 6/23/14.
 */
public interface LotTransactionRepository extends BaseRepository<LotTransaction, Long> {
    @ReadMethod
    Promise<List<LotTransaction>> getByTransactionId(Long transactionId);
}
