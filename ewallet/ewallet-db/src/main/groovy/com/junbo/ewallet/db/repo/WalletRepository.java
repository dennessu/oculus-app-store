/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.spec.def.WalletType;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.util.List;
import java.util.UUID;

/**
 * Created by minhao on 6/23/14.
 */
public interface WalletRepository extends BaseRepository<Wallet, Long> {
    @ReadMethod
    Promise<Wallet> getByTrackingUuid(Long shardMasterId, UUID uuid);

    @ReadMethod
    Promise<Wallet> get(Long userId, WalletType type, String currency);

    @ReadMethod
    Promise<List<Wallet>> getAll(Long userId);
}
