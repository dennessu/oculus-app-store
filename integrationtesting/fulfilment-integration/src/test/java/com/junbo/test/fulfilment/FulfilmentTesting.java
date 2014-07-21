/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.fulfilment;

import com.junbo.common.id.FulfilmentId;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.apache.commons.collections.map.HashedMap;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by yunlongzhao on 5/14/14.
 */
public class FulfilmentTesting extends BaseTestClass {

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
        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_digital_normal1, 1);

        String randomUid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String orderId = testDataProvider.postOrder(randomUid, country, currency, creditCardId, false, offerList);

        String fulfilmentId = testDataProvider.postFulfilment(randomUid, orderId);

        validationHelper.validateFulfilmentRequest(fulfilmentId, orderId, false);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get /fulfilments?orderId={key}",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "get fulfilment by order Id",
            steps = {
                    "1. Create a user",
                    "2. Post order with phycisal goods",
                    "3. Post fulfilment",
                    "4. Get fulfilment by order Id",
                    "5, Validation: response"
            }
    )
    @Test
    public void testGetFulfilmentByOrderId() throws Exception {
        Map<String, Integer> offerList = new HashedMap();

        offerList.put(offer_physical_normal1, 1);
        offerList.put(offer_physical_normal2, 1);

        String randomUid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String orderId = testDataProvider.postOrder(randomUid, country, currency, creditCardId, true, offerList);

        testDataProvider.postFulfilment(randomUid, orderId, true);

        String fulfilmentId = testDataProvider.getFulfilmentByOrderId(orderId);

        validationHelper.validateFulfilmentRequest(fulfilmentId, orderId, true);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Get /fulfilments/{key}",
            component = Component.Billing,
            owner = "Yunlongzhao",
            status = Status.Enable,
            description = "post fulfilment",
            steps = {
                    "1. Create a user",
                    "2. Post order",
                    "3. Post fulfilment",
                    "4. Get fulfilment by fulfilment id",
                    "3, Validation: response"
            }
    )
    @Test
    public void testGetFulfilment() throws Exception {
        Map<String, Integer> offerList = new HashedMap();
        Country country = Country.DEFAULT;
        Currency currency = Currency.DEFAULT;

        offerList.put(offer_digital_normal1, 1);

        String randomUid = testDataProvider.createUser();

        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(country);
        String creditCardId = testDataProvider.postPaymentInstrument(randomUid, creditCardInfo);

        String orderId = testDataProvider.postOrder(randomUid, country, currency, creditCardId, false, offerList);

        String fulfilmentId = testDataProvider.postFulfilment(randomUid, orderId);

        FulfilmentItem fulfilmentItem = testDataProvider.getFulfilmentItem(IdConverter.idToUrlString(
                FulfilmentId.class, Master.getInstance().
                        getFulfilment(fulfilmentId).getItems().get(0).getFulfilmentId()));

        validationHelper.validateSingleFulfilmentItem(fulfilmentItem, orderId, false);

    }

}
