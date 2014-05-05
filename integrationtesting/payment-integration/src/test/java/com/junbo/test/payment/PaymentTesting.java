/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment;


import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.payment.utility.PaymentTestDataProvider;
import com.junbo.test.payment.utility.PaymentValidationHelper;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 4/3/14.
 */
public class PaymentTesting extends BaseTestClass {
    private LogHelper logHelper = new LogHelper(PaymentTesting.class);
    private PaymentTestDataProvider testDataProvider = new PaymentTestDataProvider();
    private PaymentValidationHelper validationHelper = new PaymentValidationHelper();

    private Country country = Country.DEFAULT;


    @Property(
            priority = Priority.Dailies,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card to user",
                    "3, Validation: response",
            }
    )
    @Test
    public void testPostPaymentInstrument() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        validationHelper.validatePaymentInstrument(creditCardInfo);
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
        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        logHelper.LogSample("Get a payment instrument");
        testDataProvider.getPaymentInstrument(creditCardInfo.getPid());

        validationHelper.validatePaymentInstrument(creditCardInfo);
    }

    @Property(
            priority = Priority.Dailies,
            features = "PUT /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Disable,
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
        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);
        testDataProvider.getPaymentInstrument(creditCardId);

        logHelper.LogSample("Put a payment instrument");
        CreditCardInfo creditCardInfoForUpdate = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.updatePaymentInstrument(randomUid, creditCardId, creditCardInfoForUpdate);

        validationHelper.validatePaymentInstrument(creditCardInfo);
    }


    @Property(
            priority = Priority.Dailies,
            features = "DELETE /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.BugOnIt,
            bugNum = "https://oculus.atlassian.net/browse/SER-181",
            description = "delete payment instruments",
            steps = {
                    "1. Create an user",
                    "2. Post two credit cards to user",
                    "3  delete the first credit card",
                    "3, Validation: response & only the second credit card left"
            }
    )
    @Test
    public void testDeletePaymentInstrument() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo1);

        CreditCardInfo creditCardInfo2 = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo2);

        logHelper.LogSample("Delete a payment instrument");
        testDataProvider.deletePaymentInstruments(randomUid, creditCardInfo1.getPid());

        List<PaymentInstrumentBase> paymentList =  new ArrayList<>();
        paymentList.add(creditCardInfo2);

        validationHelper.validatePaymentInstruments(paymentList);
    }


    @Property(
            priority = Priority.Dailies,
            features = "GET /payment-instruments?userId={userId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "search payment instruments",
            steps = {
                    "1. Create an user",
                    "2. Post two credit cards to user",
                    "3. Search all payment instruments",
                    "4. Validation: response "
            }
    )
    @Test
    public void testSearchPaymentInstrument() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo1);

        CreditCardInfo creditCardInfo2 = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo2);

        logHelper.LogSample("Get payment instruments");
        testDataProvider.getPaymentInstruments(randomUid);

        List<PaymentInstrumentBase> paymentList =  new ArrayList<>();
        paymentList.add(creditCardInfo1);
        paymentList.add(creditCardInfo2);

        validationHelper.validatePaymentInstruments(paymentList);
    }

    @Property(
            priority = Priority.Dailies,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post ewallet",
            steps = {
                    "1. Create an user",
                    "2. Post ewallet to user",
                    "3, Validation: response "
            }
    )
    @Test
    public void testPostEwallet() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        testDataProvider.postPaymentInstrument(randomUid, ewalletInfo);

        logHelper.LogSample("Post ewallet");

        validationHelper.validatePaymentInstrument(ewalletInfo);
    }

    @Property(
            priority = Priority.Dailies,
            features = "POST /wallets/credit",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post ewallet",
            steps = {
                    "1. Create an user",
                    "2. Post ewallet to user",
                    "3. Credit some money,",
                    "3. Validation: response"
            }
    )
    @Test
    public void testCreditEwallet() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
        testDataProvider.postPaymentInstrument(randomUid, ewalletInfo);

        testDataProvider.creditWallet(randomUid, ewalletInfo, new BigDecimal(1000));

        logHelper.LogSample("Credit ewallet");

        validationHelper.validatePaymentInstrument(ewalletInfo);
    }

}
