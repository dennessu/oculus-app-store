/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.ewallet.db.repo;

import com.junbo.ewallet.spec.model.WalletLot;
import com.junbo.langur.core.promise.Promise;
import com.junbo.sharding.dualwrite.annotations.ReadMethod;
import com.junbo.sharding.repo.BaseRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by minhao on 6/23/14.
 */
public interface WalletLotRepository extends BaseRepository<WalletLot, Long> {
    @ReadMethod
    Promise<List<WalletLot>> getValidLot(Long walletId);

    @ReadMethod
    Promise<BigDecimal> getValidAmount(Long walletId);
}
