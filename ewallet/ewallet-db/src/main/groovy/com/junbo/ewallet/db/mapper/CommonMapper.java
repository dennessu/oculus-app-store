/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.mapper;

import com.junbo.ewallet.db.entity.def.Currency;
import com.junbo.ewallet.db.entity.def.Status;
import com.junbo.ewallet.db.entity.def.TransactionType;
import com.junbo.ewallet.db.entity.def.WalletType;
import org.springframework.stereotype.Component;

/**
 * Common Mapper.
 */
@Component("commonMapper")
public class CommonMapper {
    public String walletTypeToString(WalletType type) {
        return type.toString();
    }

    public WalletType stringToWalletType(String type) {
        return WalletType.valueOf(type);
    }

    public String statusToString(Status status) {
        return status.toString();
    }

    public Status stringToStatus(String status) {
        return Status.valueOf(status);
    }

    public String currencyToString(Currency currency) {
        return currency.toString();
    }

    public Currency stringToCurrency(String currency) {
        return Currency.valueOf(currency);
    }

    public String transactionTypeToString(TransactionType type) {
        return type.toString();
    }
}
