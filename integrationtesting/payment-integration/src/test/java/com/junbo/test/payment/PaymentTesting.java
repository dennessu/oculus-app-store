/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment;


import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.payment.utility.PaymentTestDataProvider;
import com.junbo.test.payment.utility.PaymentValidationHelper;
import org.testng.annotations.Test;

/**
 * Created by Yunlong on 4/3/14.
 */
public class PaymentTesting extends BaseTestClass {
    private LogHelper logHelper = new LogHelper(PaymentTesting.class);
    private PaymentTestDataProvider testDataProvider = new PaymentTestDataProvider();
    private PaymentValidationHelper validationHelper = new PaymentValidationHelper();


    @Property(
            priority = Priority.Dailies,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post payment instrument",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card to user",
                    "3, Validation: response",
            }
    )
    @Test
    public void testPostPaymentInstrument() throws Exception {

    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "get payment instruments",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card to user",
                    "3  Get the payment by payment response id",
                    "3, Validation: response"
            }
    )
    @Test
    public void testGetPaymentInstrument() throws Exception {

    }

    @Property(
            priority = Priority.Dailies,
            features = "PUT /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "put payment instruments",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card for user",
                    "3  Put updated payment info by payment response id",
                    "3, Validation: response"
            }
    )
    @Test
    public void testPutPaymentInstrument() throws Exception {

    }


    @Property(
            priority = Priority.Dailies,
            features = "DELETE /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "delete payment instruments",
            steps = {
                    "1. Create an user",
                    "2. Post two credit cards to user",
                    "3  delete the first credit card",
                    "3, Validation: response & one the second credit card left"
            }
    )
    @Test
    public void deletePaymentInstrument() throws Exception {

    }



    @Property(
            priority = Priority.Dailies,
            features = "GET /users/{userId}/payment-instruments/search",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "delete payment instruments",
            steps = {
                    "1. Create an user",
                    "2. Post two credit cards to user",
                    "3  Search all payment instruments",
                    "3, Validation: response "
            }
    )
    @Test
    public void searchPaymentInstrument() throws Exception {

    }


}
