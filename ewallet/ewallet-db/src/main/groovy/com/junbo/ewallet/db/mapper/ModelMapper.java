/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.mapper;

import com.junbo.ewallet.db.entity.hibernate.TransactionEntity;
import com.junbo.ewallet.db.entity.hibernate.WalletEntity;
import com.junbo.ewallet.spec.model.Transaction;
import com.junbo.ewallet.spec.model.Wallet;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;
import org.springframework.stereotype.Component;

/**
 * Mapper for Wallet.
 */
@Mapper(uses = {
        CommonMapper.class
})
@Component("modelMapper")
public interface ModelMapper {
    @Mappings({
            @Mapping(source = "id", target = "walletId", excluded = false, bidirectional = false),
    })
    Wallet toWallet(WalletEntity walletEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "walletId", target = "id", excluded = false, bidirectional = false),
    })
    WalletEntity toWalletEntity(Wallet wallet, MappingContext context);

    Transaction toTransaction(TransactionEntity transactionEntity, MappingContext context);

}
