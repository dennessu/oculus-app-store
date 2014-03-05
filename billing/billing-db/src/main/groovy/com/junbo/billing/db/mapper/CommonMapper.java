/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db.mapper;

import com.junbo.billing.spec.enums.BalanceType;
import com.junbo.billing.spec.enums.TaxAuthority;
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
