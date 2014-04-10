/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing;

import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.billing.utility.*;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.ArrayList;

/**
 * Created by Yunlong on 4/8/14.
 */
public class BillingTesting extends BaseTestClass{
      private LogHelper logHelper = new LogHelper(BillingTesting.class);

      private Country country = Country.DEFAULT;
      private Currency currency = Currency.DEFAULT;


    @Property(
            priority = Priority.Dailies,
            features = "POST /balances",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post balance by order Id",
            steps = {
                    "1. Prepare an order",
                    "2. Post balance by order Id",
                    "3, Validation: response",
            }
    )
    @Test
    public void testPostBalance() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String orderId= testDataProvider.postOrder(randomUid,country,currency,creditCardId, null,offerList);

        String balanceId = testDataProvider.postBalanceByOrderId(randomUid,orderId);

        //TODO Validate response
    }


}
