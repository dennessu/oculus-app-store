/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.mapper;

import com.junbo.ewallet.db.entity.*;
import com.junbo.ewallet.spec.model.*;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;

/**
 * Created by haomin on 14-6-20.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {
    @Mappings({
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertWalletType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertStatus"),
    })
    WalletEntity toWalletEntity(Wallet wallet, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertWalletType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertStatus"),
    })
    Wallet toWallet(WalletEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertLotType"),
    })
    WalletLotEntity toWalletLotEntity(WalletLot walletLot, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertLotType"),
    })
    WalletLot toWalletLot(WalletLotEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertTransactionType"),
    })
    TransactionEntity toTransactionEntity(Transaction transaction, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertTransactionType"),
    })
    Transaction toTransaction(TransactionEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertTransactionType"),
    })
    LotTransactionEntity toLotTransactionEntity(LotTransaction transaction, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertTransactionType"),
    })
    LotTransaction toLotTransaction(LotTransactionEntity entity, MappingContext context);
}
