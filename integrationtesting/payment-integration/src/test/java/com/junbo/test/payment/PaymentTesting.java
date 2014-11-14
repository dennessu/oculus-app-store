/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment;


import com.junbo.common.id.UserId;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.enums.PaymentType;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PayPalInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.libs.RandomFactory;
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
import java.util.Locale;

/**
 * Created by Yunlong on 4/3/14.
 */
public class PaymentTesting extends BaseTestClass {
    private LogHelper logHelper = new LogHelper(PaymentTesting.class);
    private PaymentTestDataProvider testDataProvider = new PaymentTestDataProvider();
    private PaymentValidationHelper validationHelper = new PaymentValidationHelper();

    private Country country = Country.DEFAULT;


    @Property(
            priority = Priority.BVT,
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
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card with invalid billing address",
                    "3. Validation: response error code and message",
            }
    )
    @Test
    public void testPostPIWithInvalidBillingAddress() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, IdConverter.hexStringToId(UserId.class, randomUid), 500);

        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("User Personal Info Not Found"), true, "verify error msg while post pi with invalid billing address");
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card with invalid type",
                    "3. Validation: response error code and message",
            }
    )
    @Test
    public void testPostPIWithInvalidType() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        creditCardInfo.setType(PaymentType.FAKE);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, 412);

        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("Invalid PI Type"),
                true, "verify error msg while post pi with invalid tpye");
        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("The payment instrument type 5 is invalid or not allowed"),
                true, "verify error msg while post pi with invalid tpye");
        //validate error response
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Disable,
            bugNum = "https://oculus.atlassian.net/browse/SER-766",
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card with invalid account name",
                    "3. Validation: response error code and message",
            }
    )
    @Test
    public void testPostPIWithIllegalAccountName() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        creditCardInfo.setAccountName("#_/");
        //creditCardInfo.setAccountName("#$%^&*");
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        validationHelper.validatePaymentInstrument(creditCardInfo);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card without account name",
                    "3. Validation: response error code and message",
            }
    )
    @Test
    public void testPostPIWithoutAccountName() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        creditCardInfo.setAccountName("");
        //creditCardInfo.setAccountName("#$%^&*");
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, 400);

        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("Field is required"),
                true, "verify error msg while post cc without acount name");
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card with invalid account name",
                    "3. Validation: response error code and message",
            }
    )
    @Test
    public void testPostPIWithOverLengthAccountName() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        creditCardInfo.setAccountName(RandomFactory.getRandomStringOfAlphabet(101));
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, 400);

        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("account name too long"),
                true, "verify error msg while account name over length");
        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("Field value is invalid"),
                true, "verify error msg while account name over length");
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card with invalid account num",
                    "3. Validation: response error code and message",
            }
    )
    @Test
    public void testPostPIWithInvalidAccountNum() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        creditCardInfo.setAccountNum("1234567890");
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, 500);

        assert Master.getInstance().getApiErrorMsg().contains("The provider AdyenCC process with error code");
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card without num",
                    "3. Validation: response error code and message",
            }
    )
    @Test
    public void testPostPIWithoutAccountNum() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        creditCardInfo.setAccountNum("");
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, 500);

        assert Master.getInstance().getApiErrorMsg().contains("The provider AdyenCC process with error code");
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card with invalid user",
                    "3, Validation: response",
            }
    )
    @Test
    public void testPostPIWithAnotherUser() throws Exception {
        String randomUid = testDataProvider.CreateUser();
        testDataProvider.CreateUser();

        logHelper.LogSample("Create a payment instrument");
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, 403);

        assert Master.getInstance().getApiErrorMsg().contains("Forbidden");
    }


    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post credit card",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card with expired date to user",
                    "3, Validation: response",
            }
    )
    @Test
    public void testPostCreditCardWithExpiredDate() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getExpiredCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo, 500);

        assert Master.getInstance().getApiErrorMsg().contains("The provider AdyenCC process with error code");

    }

    @Property(
            priority = Priority.BVT,
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
            features = "GET /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "get payment instruments by invalid payment id",
            steps = {
                    "1. Get the payment by invalid payment id",
                    "2. Validation: response"
            }
    )
    @Test
    public void testGetPaymentInstrumentByInvalidId() throws Exception {
        testDataProvider.getPaymentInstrument("1355efb43c9f", 404);
        assert Master.getInstance().getApiErrorMsg().contains("Payment Instrument Not Found");
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
        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);
        testDataProvider.getPaymentInstrument(creditCardId);

        logHelper.LogSample("Put a payment instrument");
        CreditCardInfo creditCardInfoForUpdate = creditCardInfo;
        String creditCreditId2 = testDataProvider.updatePaymentInstrument(randomUid, creditCardId, creditCardInfoForUpdate);

        assert creditCardId.equals(creditCreditId2);
        validationHelper.validatePaymentInstrument(creditCardInfoForUpdate);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "PUT /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "put payment instruments",
            steps = {
                    "1. Create an user",
                    "2. Post a credit card for user",
                    "3  Put payment fields which not allowed",
                    "3, Validation: response"
            }
    )
    @Test
    public void testPutPIFieldsNotAllowed() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);
        testDataProvider.getPaymentInstrument(creditCardId);

        logHelper.LogSample("Put a payment instrument");
        Master.getInstance().getPaymentInstrument(creditCardId).setType(4l);

        testDataProvider.updatePaymentInstrument(randomUid, creditCardId, creditCardInfo, 412);

        assert Master.getInstance().getApiErrorMsg().contains("invalid field to update");
        assert Master.getInstance().getApiErrorMsg().contains("type is not allow to be updated");
    }


    @Property(
            priority = Priority.BVT,
            features = "DELETE /users/{userId}/payment-instruments/{paymentInstrumentId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
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

        Master.getInstance().getPaymentInstruments().clear();
        testDataProvider.getPaymentInstruments(randomUid);

        List<PaymentInstrumentBase> paymentList = new ArrayList<>();
        paymentList.add(creditCardInfo2);

        validationHelper.validatePaymentInstruments(paymentList);
    }


    @Property(
            priority = Priority.BVT,
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
    public void testSearchPaymentInstrumentByUserId() throws Exception {
        String randomUid = testDataProvider.CreateUser();
        Master.getInstance().getPaymentInstruments().clear();

        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo1);

        CreditCardInfo creditCardInfo2 = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo2);

        logHelper.LogSample("Get payment instruments");
        testDataProvider.getPaymentInstruments(randomUid);

        List<PaymentInstrumentBase> paymentList = new ArrayList<>();
        paymentList.add(creditCardInfo1);
        paymentList.add(creditCardInfo2);

        validationHelper.validatePaymentInstruments(paymentList);
    }

    @Property(
            priority = Priority.BVT,
            features = "GET /payment-instruments?userId={userId}&type=CREDITCARD",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "search payment instruments by type",
            steps = {
                    "1. Create an user",
                    "2. Post credit card to user",
                    "3. Post paypal to user",
                    "3. Search payment instruments by type : CREDITCARD",
                    "4. Validation: response "
            }
    )
    @Test
    public void testSearchPaymentInstrumentByType() throws Exception {
        String randomUid = testDataProvider.CreateUser();
        Master.getInstance().getPaymentInstruments().clear();

        CreditCardInfo creditCardInfo1 = CreditCardInfo.getRandomCreditCardInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, creditCardInfo1);

        PayPalInfo payPalInfo = PayPalInfo.getPayPalInfo(country);
        testDataProvider.postPaymentInstrument(randomUid, payPalInfo);

        Master.getInstance().initializePayments();

        logHelper.LogSample("Get payment instruments");
        testDataProvider.getPaymentInstruments(randomUid, PaymentType.CREDITCARD.toString(), 200);

        List<PaymentInstrumentBase> paymentList = new ArrayList<>();
        paymentList.add(creditCardInfo1);

        validationHelper.validatePaymentInstruments(paymentList);
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /payment-instruments?userId={userId}",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "search payment instruments by invalid type",
            steps = {
                    "1. Create an user",
                    "2. Post two credit cards to user",
                    "3. Search all payment instruments",
                    "4. Validation: response "
            }
    )
    @Test
    public void testSearchPaymentInstrumentByInvalidType() throws Exception {
        String randomUid = testDataProvider.CreateUser();
        Master.getInstance().getPaymentInstruments().clear();

        logHelper.LogSample("Get payment instruments");
        testDataProvider.getPaymentInstruments(randomUid, "credit", 412);

        assert Master.getInstance().getApiErrorMsg().contains("Invalid PI Type");
        assert Master.getInstance().getApiErrorMsg().contains("The payment instrument type credit is invalid or not allowed");
    }

    @Property(
            priority = Priority.BVT,
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
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post ewallet",
            steps = {
                    "1. Create an user",
                    "2. Post ewallet with invalid currency",
                    "3, Validation: response "
            }
    )
    @Test
    public void testPostEwalletWithInvalidCurrency() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.FAKE);
        testDataProvider.postPaymentInstrument(randomUid, ewalletInfo, 412);

        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("Currency with ID Usd is not found"),
                true, "verify error msg while post ewallet with invalid currency");
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "POST /users/{userId}/payment-instruments",
            component = Component.Payment,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post ewallet",
            steps = {
                    "1. Create an user",
                    "2. Post ewallet without currency",
                    "3, Validation: response "
            }
    )
    @Test
    public void testPostEwalletWithoutCurrency() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        EwalletInfo ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.NULL);
        testDataProvider.postPaymentInstrument(randomUid, ewalletInfo, 400);

        ValidationHelper.verifyEqual(Master.getInstance().getApiErrorMsg().contains("Field is required"),
                true, "verify error msg while post ewallet without currency code");

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


    @Property(
            priority = Priority.Dailies,
            features = "initial user",
            component = Component.Payment,
            environment = "release",
            owner = "Yunlongzhao",
            status = Status.Disable,
            description = "prepare onebox user data",
            steps = {
                    "1. Prepare 10 users",
            }
    )
    @Test
    public void prepareOneBoxUsers() throws Exception {
        final String userPrefix = "user";
        final String password = "Test1234";
        final String emailAddress = "onebox_user#@163.com";
        List<String> userList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            List<String> uidList = testDataProvider.GetUserByUserName(userPrefix + i);

            if (uidList.size() != 0) {
                userList.add(uidList.get(0));
            } else {

                userList.add(testDataProvider.CreateUser(
                        userPrefix + i, password, emailAddress.replace("#", String.valueOf(i))));
            }

            testDataProvider.postEmailVerification(userList.get(i - 1), Country.DEFAULT.toString(), "en_US");
        }

        for (int i = 1; i <= 5; i++) {
            Master.getInstance().setCurrentUid(userList.get(i - 1));
            CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
            testDataProvider.postPaymentInstrument(userList.get(i - 1), creditCardInfo);
        }

        for (int i = 3; i <= 7; i++) {
            EwalletInfo ewalletInfo;
            if (i == 4) {
                ewalletInfo = EwalletInfo.getEwalletInfo(Country.DE, Currency.EUR);
            } else {
                ewalletInfo = EwalletInfo.getEwalletInfo(Country.DEFAULT, Currency.DEFAULT);
            }
            Master.getInstance().setCurrentUid(userList.get(i - 1));
            testDataProvider.postPaymentInstrument(userList.get(i - 1), ewalletInfo);
            testDataProvider.creditWallet(userList.get(i - 1), ewalletInfo, new BigDecimal(100));
        }

    }


}
