/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.mapper;

import com.junbo.oom.core.Mapper;
import com.junbo.oom.core.Mapping;
import com.junbo.oom.core.MappingContext;
import com.junbo.oom.core.Mappings;
import com.junbo.payment.db.entity.payment.*;
import com.junbo.payment.db.entity.paymentinstrument.CreditCardPaymentInstrumentEntity;
import com.junbo.payment.db.entity.paymentinstrument.FacebookPaymentAccountMappingEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentTypeEntity;
import com.junbo.payment.spec.internal.FacebookPaymentAccountMapping;
import com.junbo.payment.spec.internal.SettlementDetail;
import com.junbo.payment.spec.model.*;

/**
 * oom mapper model.
 */
@Mapper(uses = {
        CommonMapper.class,
})
public interface PaymentMapper {

    CreditCardPaymentInstrumentEntity toCreditCardEntity(CreditCardDetail ccRequest, MappingContext context);

    CreditCardDetail toCreditCardDetail(CreditCardPaymentInstrumentEntity ccEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "type", target = "eventTypeId", explicitMethod = "convertPaymentEventType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertPaymentStatus"),
    })
    PaymentEventEntity toPaymentEventEntityRaw(PaymentEvent event, MappingContext context);

    @Mappings({
            @Mapping(source = "eventTypeId", target = "type", explicitMethod = "convertPaymentEventType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertPaymentStatus"),
    })
    PaymentEvent toPaymentEventRaw(PaymentEventEntity eventEntity, MappingContext context);


    @Mappings({
            @Mapping(source = "merchantAccount", target = "merchantAccountId",
                    explicitMethod = "toMerchantId", bidirectional = false),
            @Mapping(source = "paymentProvider", target = "paymentProviderId",
                    explicitMethod = "toProviderId", bidirectional = false),
            @Mapping(source = "type", target = "typeId", explicitMethod = "convertPaymentType"),
            @Mapping(source = "status", target = "statusId", explicitMethod = "convertPaymentStatus"),
    })
    PaymentTransactionEntity toPaymentEntityRaw(PaymentTransaction request, MappingContext context);

    @Mappings({
            @Mapping(source = "merchantAccountId", target = "merchantAccount",
                    explicitMethod = "toMerchantName", bidirectional = false),
            @Mapping(source = "paymentProviderId", target = "paymentProvider",
                    explicitMethod = "toProviderName", bidirectional = false),
            @Mapping(source = "typeId", target = "type", explicitMethod = "convertPaymentType"),
            @Mapping(source = "statusId", target = "status", explicitMethod = "convertPaymentStatus"),
    })
    PaymentTransaction toPaymentRaw(PaymentTransactionEntity paymentTransactionEntity, MappingContext context);

    PaymentInstrumentEntity toPIEntityRaw(PaymentInstrument piRequest, MappingContext context);
    PaymentInstrument toPaymentInstrumentRaw(PaymentInstrumentEntity piEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "api", target = "apiId", explicitMethod = "convertPaymentAPI"),
    })
    TrackingUuidEntity toTrackingUuidEntity(TrackingUuid trackingUuid, MappingContext context);

    @Mappings({
            @Mapping(source = "apiId", target = "api", explicitMethod = "convertPaymentAPI"),
    })
    TrackingUuid toTrackingUuid(TrackingUuidEntity entity, MappingContext context);

    PaymentProperty toPaymentProperty(PaymentPropertyEntity entity, MappingContext context);

    PaymentPropertyEntity toPaymentPropertyEntity(PaymentProperty paymentProperty, MappingContext context);

    PaymentInstrumentTypeEntity toPITypeEntity(PaymentInstrumentType piType, MappingContext context);

    PaymentInstrumentType toPIType(PaymentInstrumentTypeEntity entity, MappingContext context);

    SettlementDetailEntity toSettlementDetailEntity(SettlementDetail settlementDetail, MappingContext context);

    SettlementDetail toSettlementDetail(SettlementDetailEntity entity, MappingContext context);

    FacebookPaymentAccountMappingEntity toFacebookPaymentAccountEntity(FacebookPaymentAccountMapping model, MappingContext context);
    FacebookPaymentAccountMapping toFacebookPaymentAccount(FacebookPaymentAccountMappingEntity entity, MappingContext context);
}
