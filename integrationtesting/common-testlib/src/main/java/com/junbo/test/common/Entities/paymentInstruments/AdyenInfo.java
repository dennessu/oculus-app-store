/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.Entities.paymentInstruments;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.PaymentType;

/**
 * Created by weiyu_000 on 6/18/14.
 */
public class AdyenInfo extends PaymentInstrumentBase {

    public AdyenInfo() {
        this.setType(PaymentType.OTHERS);
    }

    public static AdyenInfo getAdyenInfo(Country country) {
        AdyenInfo adyenInfo = new AdyenInfo();
        adyenInfo.setAccountNum("zwh@123.com");
        //adyenInfo.setPhone(Phone.getRandomPhone());
        adyenInfo.setAddress(Address.getRandomAddress(country));
        adyenInfo.setValidated(false);
        adyenInfo.setDefault(true);
        return adyenInfo;
    }

}
