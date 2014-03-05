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
import com.junbo.payment.db.entity.CountryTypeEntity;
import com.junbo.payment.db.entity.CurrencyTypeEntity;
import com.junbo.payment.db.entity.TrackingUuidEntity;
import com.junbo.payment.db.entity.payment.PaymentEntity;
import com.junbo.payment.db.entity.payment.PaymentEventEntity;
import com.junbo.payment.db.entity.paymentinstrument.AddressEntity;
import com.junbo.payment.db.entity.paymentinstrument.CreditCardPaymentInstrumentEntity;
import com.junbo.payment.db.entity.paymentinstrument.PaymentInstrumentEntity;
import com.junbo.payment.db.entity.paymentinstrument.PhoneEntity;
import com.junbo.payment.spec.model.*;

/**
 * oom mapper model.
 */
@Mapper(uses = {
        CommonMapper.class,
})
public interface PaymentMapper {
    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false),
    })
    AddressEntity toAddressEntity(Address address, MappingContext context);

    @Mappings({
            @Mapping(source = "countryTypeId", excluded = true, bidirectional = false),
    })
    Address toAddress(AddressEntity addressEntity, MappingContext context);

    CreditCardPaymentInstrumentEntity toCreditCardEntity(CreditCardRequest ccRequest, MappingContext context);

    CreditCardRequest toCreditCardRequest(CreditCardPaymentInstrumentEntity ccEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false),
    })
    PhoneEntity toPhoneEntity(Phone phone, MappingContext context);

    Phone toPhone(PhoneEntity phoneEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false),
    })
    CountryTypeEntity toCountryEntity(Country country, MappingContext context);

    Country toCountry(CountryTypeEntity countryEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false),
    })
    CurrencyTypeEntity toCurrencyEntity(Currency currency, MappingContext context);

    Currency toCurrency(CurrencyTypeEntity currencyEntity, MappingContext context);

    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false)
    })
    PaymentEventEntity toPaymentEventEntityRaw(PaymentEvent event, MappingContext context);

    PaymentEvent toPaymentEventRaw(PaymentEventEntity eventEntity, MappingContext context);


    @Mappings({
            @Mapping(source = "merchantAccount", target = "merchantAccountId",
                    explicitMethod = "toMerchantId", bidirectional = false),
            @Mapping(source = "paymentProvider", target = "paymentProviderId",
                    explicitMethod = "toProviderId", bidirectional = false),
    })
    PaymentEntity toPaymentEntityRaw(PaymentTransaction request, MappingContext context);
    @Mappings({
            @Mapping(source = "merchantAccountId", target = "merchantAccount",
                    explicitMethod = "toMerchantName", bidirectional = false),
            @Mapping(source = "paymentProviderId", target = "paymentProvider",
                    explicitMethod = "toProviderName", bidirectional = false),
    })
    PaymentTransaction toPaymentRaw(PaymentEntity paymentEntity, MappingContext context);


    @Mappings({
            @Mapping(source = "createdTime", target = "createdTime", excluded = true, bidirectional = false),
            @Mapping(source = "updatedTime", target = "updatedTime", excluded = true, bidirectional = false),
    })
    PaymentInstrumentEntity toPIEntity(PaymentInstrument piRequest, MappingContext context);

    PaymentInstrument toPaymentInstrument(PaymentInstrumentEntity piEntity, MappingContext context);

    TrackingUuidEntity toTrackingUuidEntity(TrackingUuid trackingUuid, MappingContext context);

    TrackingUuid toTrackingUuid(TrackingUuidEntity entity, MappingContext context);


}
