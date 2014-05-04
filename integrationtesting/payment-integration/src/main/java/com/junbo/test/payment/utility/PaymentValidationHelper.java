/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.utility;

import com.junbo.common.id.UserId;

import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.ShardIdHelper;

import java.util.List;

/**
 * Created by Yunlong on 4/4/14.
 */
public class PaymentValidationHelper extends BaseValidationHelper {
    DBHelper dbHelper = new DBHelper();

    public PaymentValidationHelper() {
        super();
    }

    public void validatePaymentInstrument(String paymentId, PaymentInstrumentBase expectedPaymentInfo) throws Exception {

        PaymentInstrument actualPI = Master.getInstance().getPaymentInstrument(paymentId);
        TypeSpecificDetails typeSpecificDetails = actualPI.getTypeSpecificDetails();
        verifyEqual(actualPI.getType(), expectedPaymentInfo.getType().getValue(), "verify payment type");
        verifyEqual(actualPI.getAccountName(), expectedPaymentInfo.getAccountName(), "verify account name");
        verifyEqual(actualPI.getBillingAddressId(), expectedPaymentInfo.getBillingAddressId(),
                "verify billing addressId");
        String uid = IdConverter.idLongToHexString(UserId.class, actualPI.getUserId());
        switch (expectedPaymentInfo.getType()) {
            case CREDITCARD:
                CreditCardInfo expectedCreditCard = (CreditCardInfo) expectedPaymentInfo;
                String maskedCCNum = expectedCreditCard.getAccountNum().substring(0, 6) + "******" +
                        expectedCreditCard.getAccountNum().substring(12);
                //verify basic credit card info
                verifyEqual(actualPI.getAccountNum(), maskedCCNum, "verify credit card number");

                verifyEqual(typeSpecificDetails.getExpireDate(), expectedCreditCard.getExpireDate(),
                        "verify expire date");
                verifyEqual(typeSpecificDetails.getCreditCardType(), CreditCardInfo.CreditCardGenerator.VISA.toString(),
                        "verify credit card type");

                /* verify billing address
                Address address = actualPI.getAddress();
                verifyEqual(address.getAddressLine1(), expectedCreditCard.getAddress().getAddressLine1(),
                        "verify billing address - Line1");
                verifyEqual(address.getCity(), expectedCreditCard.getAddress().getCity(),
                        "verify billing address - City");
                verifyEqual(address.getState(), expectedCreditCard.getAddress().getState(),
                        "verify billing address - State");
                verifyEqual(address.getCountry(), expectedCreditCard.getAddress().getCountry(),
                        "verify billing address - Country");
                verifyEqual(address.getPostalCode(), expectedCreditCard.getAddress().getPostalCode(),
                        "verify billing address - PostalCode");

                //verify phone
                verifyEqual(actualPI.getPhoneNumber().toString(), expectedCreditCard.getPhone().getNumber(),
                        "verify phone number");
                        */
                //verify other info

                //verifyEqual(actualPI.getIsDefault(), String.valueOf(expectedCreditCard.isDefault()),
                //        "verify payment is default");
                break;

            case EWALLET:
                EwalletInfo ewalletInfo = (EwalletInfo) expectedPaymentInfo;
                verifyEqual(typeSpecificDetails.getStoredValueCurrency(), ewalletInfo.getCurrency().toString(), "verify wallet currency");
                verifyEqual(actualPI.getAccountName(), ewalletInfo.getAccountName(), "verify account name");

                String sqlStr = String.format(
                        "select balance from shard_%s.ewallet where user_id = '%s'",
                        ShardIdHelper.getShardIdByUid(uid), actualPI.getUserId());
                verifyEqual(dbHelper.executeScalar(sqlStr, DBHelper.DBName.EWALLET),
                        ewalletInfo.getBalance().toString(), "verify ewallet balance");

                break;

            default:
                throw new TestException(String.format("%s : %s is not supported",
                        expectedPaymentInfo.getAccountName(), expectedPaymentInfo.getAccountNum()));
        }

    }

    public void validatePaymentInstruments(List<String> paymentList, List<PaymentInstrumentBase> expectedPaymentList) {
        verifyEqual(paymentList.size(), expectedPaymentList.size(), "verify payment list size");
        for (int i = 0; i < paymentList.size(); i++) {
            //TODO Need Sort
            // validatePaymentInstrument(paymentList.get(i), expectedPaymentList.get(i));
        }
    }

}
