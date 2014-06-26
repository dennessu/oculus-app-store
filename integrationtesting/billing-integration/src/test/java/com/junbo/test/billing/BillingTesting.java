/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.billing;

import com.junbo.test.common.Entities.ShippingAddressInfo;
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
public class BillingTesting extends BaseTestClass {
    private LogHelper logHelper = new LogHelper(BillingTesting.class);

    private Country country = Country.DEFAULT;
    private Currency currency = Currency.DEFAULT;

    @Property(
            priority = Priority.Dailies,
            features = "POST /balances/quote",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post balance by order Id",
            steps = {
                    "1. Create a user",
                    "2. Post quote balance",
                    "3, Validation: response",
            }
    )
    @Test
    public void testQuoteBalance() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String fakeBalanceId = testDataProvider.quoteBalance(randomUid, creditCardId);

        validationHelper.validateBalanceQuote(randomUid, fakeBalanceId, creditCardId);
    }


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

        String orderId = testDataProvider.postOrder(randomUid, country, currency, creditCardId, null, offerList);

        String balanceId = testDataProvider.postBalanceByOrderId(randomUid, orderId);

        validationHelper.validateBalance(randomUid, balanceId, orderId, true);
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /balances?orderId=",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "Get balance by order Id",
            steps = {
                    "1. Prepare an order",
                    "2. Update order tentative to false",
                    "3, Get balance by order Id",
                    "4, Validation: response"
            }
    )
    @Test
    public void testGetBalanceByOrderId() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String orderId = testDataProvider.postOrder(randomUid, country, currency, creditCardId, null, offerList);

        orderId = testDataProvider.updateOrderTentative(orderId, false);

        String balanceId = testDataProvider.getBalancesByOrderId(orderId).get(0);

        validationHelper.validateBalance(randomUid, balanceId, orderId, false);
    }

    @Property(
            priority = Priority.Dailies,
            features = "GET /balances/balanceId",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "Get balance by balance Id",
            steps = {
                    "1. Prepare an order",
                    "2. Update order tentative to false",
                    "3, Get balance by order Id",
                    "4, Validation: response"
            }
    )
      @Test
      public void testGetBalanceByBalanceId() throws Exception {
        ArrayList<String> offerList = new ArrayList<>();
        offerList.add(offer_digital_normal1);
        offerList.add(offer_digital_normal2);

        String randomUid = testDataProvider.CreateUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String orderId = testDataProvider.postOrder(randomUid, country, currency, creditCardId, null, offerList);

        String balanceId = testDataProvider.postBalanceByOrderId(randomUid, orderId);

        balanceId = testDataProvider.getBalanceByBalanceId(randomUid, balanceId);

        validationHelper.validateBalance(randomUid, balanceId, orderId, false);
    }


    @Property(
            priority = Priority.Dailies,
            features = "POST /user/userId/ship-to-info",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post shipping address",
            steps = {
                    "1. Create a user",
                    "2. Post shipping address to user",
                    "3, Validation: response"
            }
    )
    @Test
    public void testPostShippingAddress() throws Exception {
        String randomUid = testDataProvider.CreateUser();

        ShippingAddressInfo shippingAddressInfo = ShippingAddressInfo.getRandomShippingAddress(Country.DEFAULT);
        //String shippingAddressId = testDataProvider.postShippingAddressToUser(randomUid, shippingAddressInfo);

        //TODO Validate response
    }










}
