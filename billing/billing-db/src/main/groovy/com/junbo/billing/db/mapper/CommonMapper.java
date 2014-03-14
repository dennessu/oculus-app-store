/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.mapper;

import com.junbo.billing.spec.enums.*;
import com.junbo.common.id.*;
import com.junbo.common.util.EnumRegistry;
import org.springframework.util.StringUtils;

/**
 * Created by xmchen on 14-2-14.
 */
public class CommonMapper {
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

    public Long toBalanceItemIdLong(BalanceItemId id) {
        return id == null ? null : id.getValue();
    }

    public Long toTaxItemIdLong(TaxItemId id) {
        return id == null ? null : id.getValue();
    }

    public Long toDiscountItemIdLong(DiscountItemId id) {
        return id == null ? null : id.getValue();
    }

    public Long toShippingAddressIdLong(ShippingAddressId id) {
        return id == null ? null : id.getValue();
    }

    public Long toBillingAddressIdLong(BillingAddressId id) {
        return id == null ? null : id.getValue();
    }

    public Long toPaymentInstrumentIdLong(PaymentInstrumentId id) {
        return id == null ? null : id.getValue();
    }

    public Long toUserIdLong(UserId id) {
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

    public BalanceItemId toBalanceItemId(Long id) {
        return id == null ? null : new BalanceItemId(id);
    }

    public TaxItemId toTaxItemId(Long id) {
        return id == null ? null : new TaxItemId(id);
    }

    public DiscountItemId toDiscountItemId(Long id) {
        return id == null ? null : new DiscountItemId(id);
    }

    public ShippingAddressId toShippingAddressId(Long id) {
        return id == null ? null : new ShippingAddressId(id);
    }

    public BillingAddressId toBillingAddressId(Long id) {
        return id == null ? null : new BillingAddressId(id);
    }

    public PaymentInstrumentId toPaymentInstrumentId(Long id) {
        return id == null ? null : new PaymentInstrumentId(id);
    }

    public UserId toUserId(Long id) {
        return id == null ? null : new UserId(id);
    }

    public OrderItemId toOrderItemId(Long id) {
        return id == null ? null : new OrderItemId(id);
    }

    public TransactionId toTransactionId(Long id) {
        return id == null ? null : new TransactionId(id);
    }
}
