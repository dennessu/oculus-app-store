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

    public void validatePaymentInstrument(PaymentInstrumentBase expectedPaymentInfo) throws Exception {

        PaymentInstrument actualPI = Master.getInstance().getPaymentInstrument(expectedPaymentInfo.getPid());
        TypeSpecificDetails typeSpecificDetails = actualPI.getTypeSpecificDetails();
        verifyEqual(actualPI.getType(), expectedPaymentInfo.getType().getValue(), "verify payment type");
        verifyEqual(actualPI.getAccountName(), expectedPaymentInfo.getAccountName(), "verify account name");
        verifyEqual(actualPI.getBillingAddressId(), expectedPaymentInfo.getBillingAddressId(),
                "verify billing addressId");
        String uid = IdConverter.idToUrlString(UserId.class, actualPI.getUserId());
        switch (expectedPaymentInfo.getType()) {
            case CREDITCARD:
                CreditCardInfo expectedCreditCard = (CreditCardInfo) expectedPaymentInfo;
                String maskedCCNum = expectedCreditCard.getAccountNum().substring(0, 6) + "******" +
                        expectedCreditCard.getAccountNum().substring(12);
                //verify basic credit card info
                verifyEqual(actualPI.getAccountNumber(), maskedCCNum, "verify credit card number");

                verifyEqual(typeSpecificDetails.getExpireDate(), expectedCreditCard.getExpireDate(),
                        "verify expire date");
                verifyEqual(typeSpecificDetails.getCreditCardType(), CreditCardInfo.CreditCardGenerator.VISA.toString(),
                        "verify credit card type");

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

    public void validatePaymentInstruments(List<PaymentInstrumentBase> expectedPaymentList) throws Exception {
        verifyEqual(Master.getInstance().getPaymentInstruments().size(),

                expectedPaymentList.size(), "verify payments quantity");
        for (int i = 0; i < expectedPaymentList.size(); i++) {
            validatePaymentInstrument(expectedPaymentList.get(i));
        }
    }

}
