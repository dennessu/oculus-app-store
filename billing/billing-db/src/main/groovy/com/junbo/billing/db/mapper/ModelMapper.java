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
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertBalanceType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertBalanceStatus"),
            @Mapping(source = "taxStatus", target = "taxStatusId", explicitMethod = "convertTaxStatus"),
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
    })
    BalanceEntity toBalanceEntity(Balance balance, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertBalanceType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertBalanceStatus"),
            @Mapping(source = "taxStatusId", target = "taxStatus", explicitMethod = "convertTaxStatus"),
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
    })
    Balance toBalance(BalanceEntity entity, MappingContext context);

    BalanceEventEntity toBalanceEventEntity(BalanceEvent balanceEvent, MappingContext context);

    BalanceEvent toBalanceEvent(BalanceEventEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
    })
    BalanceItemEntity toBalanceItemEntity(BalanceItem balanceItem, MappingContext context);

    @Mappings({
            @Mapping(source = "propertySet", target = "propertySet", explicitMethod = "convertPropertySet"),
    })
    BalanceItem toBalanceItem(BalanceItemEntity entity, MappingContext context);

    BalanceItemEventEntity toBalanceItemEventEntity(BalanceItemEvent balanceItemEvent, MappingContext context);
    BalanceItemEvent toBalanceItemEvent(BalanceItemEventEntity entity, MappingContext context);


    DiscountItemEntity toDiscountItemEntity(DiscountItem discountItem, MappingContext context);
    DiscountItem toDiscountItem(DiscountItemEntity entity, MappingContext context);


    OrderBalanceLinkEntity toOrderBalanceLinkEntity(OrderBalanceLink orderBalanceLink, MappingContext context);
    OrderBalanceLink toOrderBalanceLink(OrderBalanceLinkEntity entity, MappingContext context);


    @Mappings({
            @Mapping(source = "taxAuthority", target = "taxAuthorityId", explicitMethod = "convertTaxAuthority"),
    })
    TaxItemEntity toTaxItemEntity(TaxItem taxItem, MappingContext context);

    @Mappings({
            @Mapping(source = "taxAuthorityId", target = "taxAuthority", explicitMethod = "convertTaxAuthority"),
    })
    TaxItem toTaxItem(TaxItemEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertTransactionType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertTransactionStatus"),
    })
    Transaction toTransaction(TransactionEntity entity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertTransactionType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertTransactionStatus"),
    })
    TransactionEntity toTransactionEntity(Transaction transaction, MappingContext context);
}
