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
public class PayPalInfo extends PaymentInstrumentBase {

    public PayPalInfo() {
        this.setType(PaymentType.PAYPAL);
    }

    public static PayPalInfo getPayPalInfo(Country country) {
        PayPalInfo payPalInfo = new PayPalInfo();
        payPalInfo.setAccountName(RandomFactory.getRandomStringOfAlphabet(10));
        //"zwh@123.com" is a test account on https://www.sandbox.paypal.com/
        payPalInfo.setAccountNum("zwh@123.com");
        //payPalInfo.setPhone(Phone.getRandomPhone());
        payPalInfo.setAddress(Address.getRandomAddress(country));
        payPalInfo.setValidated(false);
        payPalInfo.setDefault(true);
        return payPalInfo;
    }

}
