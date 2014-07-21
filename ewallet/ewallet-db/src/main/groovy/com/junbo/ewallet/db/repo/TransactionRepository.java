/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by minhao on 6/23/14.
 */
public interface TransactionRepository extends BaseRepository<Transaction, Long> {
    @ReadMethod
    Promise<List<Transaction>> getByWalletId(Long walletId);

    @ReadMethod
    Promise<Transaction> getByTrackingUuid(Long shardMasterId, UUID uuid);
}
