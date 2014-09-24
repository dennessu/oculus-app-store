/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.billing.spec.enums.*;
import com.junbo.common.id.*;
import com.junbo.common.util.EnumRegistry;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xmchen on 14-2-14.
 */
public class CommonMapper {

    private ObjectMapper mapper = new ObjectMapper();

    public Short explicitMethod_convertBalanceType(String type) {
        if(!StringUtils.isEmpty(type)) {
            BalanceType balanceType = BalanceType.valueOf(type);
            return balanceType.getId();
        }
        return null;
    }

    public String explicitMethod_convertBalanceType(Short typeId) {
        BalanceType balanceType = EnumRegistry.resolve(typeId, BalanceType.class);
        return balanceType.toString();
    }

    public Short explicitMethod_convertTaxStatus(String taxStatus) {
        if(!StringUtils.isEmpty(taxStatus)) {
            TaxStatus status = TaxStatus.valueOf(taxStatus);
            return status.getId();
        }
        return null;
    }

    public String explicitMethod_convertTaxStatus(Short taxStatusId) {
        TaxStatus status = EnumRegistry.resolve(taxStatusId, TaxStatus.class);
        return status.toString();
    }

    public Short explicitMethod_convertTaxAuthority(String taxAuthority) {
        if(!StringUtils.isEmpty(taxAuthority)) {
            TaxAuthority tax = TaxAuthority.valueOf(taxAuthority);
            return tax.getId();
        }
        return null;
    }

    public String explicitMethod_convertTaxAuthority(Short taxAuthorityId) {
        TaxAuthority tax = EnumRegistry.resolve(taxAuthorityId, TaxAuthority.class);
        return tax.toString();
    }

    public Short explicitMethod_convertTransactionType(String type) {
        if(!StringUtils.isEmpty(type)) {
            TransactionType transactionType = TransactionType.valueOf(type);
            return transactionType.getId();
        }
        return null;
    }

    public String explicitMethod_convertTransactionType(Short typeId) {
        TransactionType transactionType = EnumRegistry.resolve(typeId, TransactionType.class);
        return transactionType.toString();
    }

    public Short explicitMethod_convertBalanceStatus(String status) {
        if(!StringUtils.isEmpty(status)) {
            BalanceStatus balanceStatus = BalanceStatus.valueOf(status);
            return balanceStatus.getId();
        }
        return null;
    }

    public String explicitMethod_convertBalanceStatus(Short statusId) {
        BalanceStatus balanceStatus = EnumRegistry.resolve(statusId, BalanceStatus.class);
        return balanceStatus.toString();
    }

    public Short explicitMethod_convertTransactionStatus(String status) {
        if(!StringUtils.isEmpty(status)) {
            TransactionStatus transactionStatus = TransactionStatus.valueOf(status);
            return transactionStatus.getId();
        }
        return null;
    }

    public String explicitMethod_convertTransactionStatus(Short statusId) {
        TransactionStatus transactionStatus = EnumRegistry.resolve(statusId, TransactionStatus.class);
        return transactionStatus.toString();
    }

    public Long toBalanceIdLong(BalanceId id) {
        return id == null ? null : id.getValue();
    }

    public Long toUserPersonalInfoIdLong(UserPersonalInfoId id) {
        return id == null ? null : id.getValue();
    }

    public Long toPaymentInstrumentIdLong(PaymentInstrumentId id) {
        return id == null ? null : id.getValue();
    }

    public Long toUserIdLong(UserId id) {
        return id == null ? null : id.getValue();
    }

    public Long toOrderIdLong(OrderId id) {
        return id == null ? null : id.getValue();
    }

    public Long toOrderItemIdLong(OrderItemId id) {
        return id == null ? null : id.getValue();
    }

    public Long toTransactionIdLong(TransactionId id) {
        return id == null ? null : id.getValue();
    }

    public BalanceId toBalanceId(Long id) {
        return id == null ? null : new BalanceId(id);
    }

    public UserPersonalInfoId toUserPersonalInfoId(Long id) {
        return id == null ? null : new UserPersonalInfoId(id);
    }

    public PaymentInstrumentId toPaymentInstrumentId(Long id) {
        return id == null ? null : new PaymentInstrumentId(id);
    }

    public UserId toUserId(Long id) {
        return id == null ? null : new UserId(id);
    }

    public OrderId toOrderId(Long id) {
        return id == null ? null : new OrderId(id);
    }

    public OrderItemId toOrderItemId(Long id) {
        return id == null ? null : new OrderItemId(id);
    }

    public TransactionId toTransactionId(Long id) {
        return id == null ? null : new TransactionId(id);
    }

    public String explicitMethod_convertPropertySet(Map<String, String> propertySet) {
        try {
            return mapper.writeValueAsString(propertySet);
        }
        catch (JsonProcessingException ex) {
            return null;
        }
    }

    public Map<String, String> explicitMethod_convertPropertySet(String propertySet) {
        try {
            return mapper.readValue(propertySet, new TypeReference<HashMap<String,String>>(){});
        }
        catch (Exception ex) {
            return null;
        }
    }

    public Integer toInteger(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public String toString(Integer value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public Long fromStringToLong(String source) {
        return source == null || source.isEmpty() ? null : Long.parseLong(source);
    }

    public String fromLongToString(Long source) {
        return source == null ? null : source.toString();
    }

    public String fromShortToString(Short id) {
        if (id == null) {
            return null;
        }

        return id.toString();
    }

    public Short fromStringToShort(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }

        return Short.valueOf(id);
    }

    public Short fromShortToShort(Short source) {
        return source == null ? null : new Short(source);
    }
}
