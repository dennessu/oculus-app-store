/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.paymentInstruments;

import com.junbo.test.common.Entities.enums.PaymentType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.libs.RandomFactory;

/**
 @author Jason
  * Time: 5/6/2014
  * The PayPal info
 */
public class PayPalInfo extends CreditCardInfo {
    private String email;

    public PayPalInfo() {
        this.setType(PaymentType.PAYPAL);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static PayPalInfo getPayPalInfo(Country country) {
        PayPalInfo payPalInfo = new PayPalInfo();
        payPalInfo.setAccountName(RandomFactory.getRandomStringOfAlphabet(5));
        String[] creditCardArray = new String[]{"4111111111111111", "4012888888881881"};
        payPalInfo.setAccountNum(creditCardArray[RandomFactory.getRandomInteger(1)]);
        payPalInfo.setEncryptedCVMCode(getCVMCode(CreditCardGenerator.VISA.toString(), true));
        payPalInfo.setExpireDate(getFormattedExpireDate());
        payPalInfo.setPhone(Phone.getRandomPhone());
        payPalInfo.setAddress(Address.getRandomAddress(country));
        payPalInfo.setValidated(false);
        payPalInfo.setDefault(true);
        return payPalInfo;
    }

}
