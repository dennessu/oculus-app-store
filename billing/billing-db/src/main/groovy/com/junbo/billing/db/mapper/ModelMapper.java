/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;
import com.junbo.billing.db.entity.BalanceEntity;
import com.junbo.billing.db.entity.BalanceItemEntity;
import com.junbo.billing.db.entity.DiscountItemEntity;
import com.junbo.billing.db.entity.TaxItemEntity;
import com.junbo.billing.db.entity.CurrencyEntity;
import com.junbo.billing.db.entity.TransactionEntity;
import com.junbo.billing.spec.model.*;

/**
 * Created by xmchen on 14-2-14.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {

    @Mappings({
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    Currency toCurrency(CurrencyEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertBalanceType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertBalanceStatus"),
            @Mapping(source = "taxStatus", target = "taxStatusId", explicitMethod = "convertTaxStatus"),
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    BalanceEntity toBalanceEntity(Balance balance, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    BalanceItemEntity toBalanceItemEntity(BalanceItem balanceItem, MappingContext context);

    @Mappings({
            @Mapping(source = "taxAuthority", target = "taxAuthorityId", explicitMethod = "convertTaxAuthority"),
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    TaxItemEntity toTaxItemEntity(TaxItem taxItem, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    DiscountItemEntity toDiscountItemEntity(DiscountItem discountItem, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertBalanceType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertBalanceStatus"),
            @Mapping(source = "taxStatusId", target = "taxStatus", explicitMethod = "convertTaxStatus"),
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    Balance toBalance(BalanceEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    BalanceItem toBalanceItem(BalanceItemEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "taxAuthorityId", target = "taxAuthority", explicitMethod = "convertTaxAuthority"),
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    TaxItem toTaxItem(TaxItemEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    DiscountItem toDiscountItem(DiscountItemEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertTransactionType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertTransactionStatus"),
            @Mapping(source = "createdTime", target = "transactionTime", bidirectional = false),
    })
    Transaction toTransaction(TransactionEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertTransactionType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertTransactionStatus"),
            @Mapping(source = "createdTime", target = "insertedDate", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedDate", excluded = true, bidirectional = false),
    })
    TransactionEntity toTransactionEntity(Transaction transaction, MappingContext context);
}
