/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.paymentInstruments;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.enums.PaymentType;
import com.junbo.test.common.libs.RandomFactory;

/**
 * Created by Yunlong on 4/24/14.
 */
public class EwalletInfo extends PaymentInstrumentBase {
    private String walletType;
    private Currency currency;

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        walletType = walletType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public EwalletInfo() {
        super();
        this.setType(PaymentType.EWALLET);
    }

    public static EwalletInfo getEwalletInfo(Country country, Currency currency) {
        EwalletInfo ewalletInfo = new EwalletInfo();
        ewalletInfo.setCurrency(currency);
        ewalletInfo.setAccountName(RandomFactory.getRandomStringOfAlphabet(5));
        ewalletInfo.setAddress(Address.getRandomAddress(country));
        ewalletInfo.setValidated(false);
        ewalletInfo.setWalletType(String.format("STORED_VALUE"));
        return ewalletInfo;
    }


}
