/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.mapper;

import com.junbo.billing.db.entity.*;
import com.junbo.billing.spec.model.*;
import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;

/**
 * Created by xmchen on 14-2-14.
 */
@Mapper(uses = {
        CommonMapper.class
})
public interface ModelMapper {
    @Mappings({
            @Mapping(source = "id", target = "balanceId", excluded = false, bidirectional = false),
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertBalanceType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertBalanceStatus"),
            @Mapping(source = "taxStatus", target = "taxStatusId", explicitMethod = "convertTaxStatus"),
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
    })
    BalanceEntity toBalanceEntity(Balance balance, MappingContext context);

    @Mappings({
            @Mapping(source = "balanceId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertBalanceType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertBalanceStatus"),
            @Mapping(source = "taxStatusId", target = "taxStatus", explicitMethod = "convertTaxStatus"),
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
    })
    Balance toBalance(BalanceEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "eventId", excluded = false, bidirectional = false),
    })
    BalanceEventEntity toBalanceEventEntity(BalanceEvent balanceEvent, MappingContext context);

    @Mappings({
            @Mapping(source = "eventId", target = "id", excluded = false, bidirectional = false),
    })
    BalanceEvent toBalanceEvent(BalanceEventEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "balanceItemId", excluded = false, bidirectional = false),
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
            @Mapping(source = "updatedTime", target = "modifiedTime", excluded = false, bidirectional = false),
    })
    BalanceItemEntity toBalanceItemEntity(BalanceItem balanceItem, MappingContext context);

    @Mappings({
            @Mapping(source = "balanceItemId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
            @Mapping(source = "modifiedTime", target = "updatedTime", excluded = false, bidirectional = false),
    })
    BalanceItem toBalanceItem(BalanceItemEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "eventId", excluded = false, bidirectional = false),
    })
    BalanceItemEventEntity toBalanceItemEventEntity(BalanceItemEvent balanceItemEvent, MappingContext context);

    @Mappings({
            @Mapping(source = "eventId", target = "id", excluded = false, bidirectional = false),
    })
    BalanceItemEvent toBalanceItemEvent(BalanceItemEventEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "discountItemId", excluded = false, bidirectional = false),
            @Mapping(source = "updatedTime", target = "modifiedTime", excluded = false, bidirectional = false),
    })
    DiscountItemEntity toDiscountItemEntity(DiscountItem discountItem, MappingContext context);

    @Mappings({
            @Mapping(source = "discountItemId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "modifiedTime", target = "updatedTime", excluded = false, bidirectional = false),
    })
    DiscountItem toDiscountItem(DiscountItemEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "linkId", excluded = false, bidirectional = false),
            @Mapping(source = "updatedTime", target = "modifiedTime", excluded = false, bidirectional = false),
    })
    OrderBalanceLinkEntity toOrderBalanceLinkEntity(OrderBalanceLink orderBalanceLink, MappingContext context);

    @Mappings({
            @Mapping(source = "linkId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "modifiedTime", target = "updatedTime", excluded = false, bidirectional = false),
    })
    OrderBalanceLink toOrderBalanceLink(OrderBalanceLinkEntity entity, MappingContext context);


    @Mappings({
            @Mapping(source = "id", target = "taxItemId", excluded = false, bidirectional = false),
            @Mapping(source = "taxAuthority", target = "taxAuthorityId", explicitMethod = "convertTaxAuthority"),
            @Mapping(source = "updatedTime", target = "modifiedTime", excluded = false, bidirectional = false),
    })
    TaxItemEntity toTaxItemEntity(TaxItem taxItem, MappingContext context);

    @Mappings({
            @Mapping(source = "taxItemId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "taxAuthorityId", target = "taxAuthority", explicitMethod = "convertTaxAuthority"),
            @Mapping(source = "modifiedTime", target = "updatedTime", excluded = false, bidirectional = false),
    })
    TaxItem toTaxItem(TaxItemEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "transactionId", target = "id", excluded = false, bidirectional = false),
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertTransactionType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertTransactionStatus"),
            @Mapping(source = "modifiedTime", target = "updatedTime", excluded = false, bidirectional = false),
    })
    Transaction toTransaction(TransactionEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "id", target = "transactionId", excluded = false, bidirectional = false),
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertTransactionType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertTransactionStatus"),
            @Mapping(source = "updatedTime", target = "modifiedTime", excluded = false, bidirectional = false),
    })
    TransactionEntity toTransactionEntity(Transaction transaction, MappingContext context);
}
