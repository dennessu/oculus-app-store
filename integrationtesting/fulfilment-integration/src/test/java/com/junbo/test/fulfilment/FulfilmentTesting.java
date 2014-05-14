/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.fulfilment;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by yunlongzhao on 5/14/14.
 */
public class FulfilmentTesting extends BaseTestClass{

    private Country country = Country.DEFAULT;
    private Currency currency = Currency.DEFAULT;

    @Property(
            priority = Priority.Dailies,
            features = "POST /fulfilments",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post fulfilment",
            steps = {
                    "1. Create a user",
                    "2. Post order",
                    "3. Post fulfilment",
                    "3, Validation: response"
            }
    )
    @Test
    public void testPostFulfilment() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        String randomUid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String orderId = testDataProvider.postOrder(randomUid,country,currency,creditCardId,false,offerList);

        testDataProvider.postFulfilment(randomUid, orderId);

        //TODO validate response
    }

}
