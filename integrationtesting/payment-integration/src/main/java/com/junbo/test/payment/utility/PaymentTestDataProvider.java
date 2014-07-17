/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.utility;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import com.junbo.test.common.Entities.paymentInstruments.*;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.DBHelper;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.ShardIdHelper;
import com.junbo.test.payment.apihelper.PaymentService;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yunlong on 4/4/14.
 */
public class PaymentTestDataProvider extends BaseTestDataProvider {
    private UserService identityClient = UserServiceImpl.instance();
    private PaymentService paymentClient = PaymentServiceImpl.getInstance();

    public PaymentTestDataProvider() {
        super();
    }

    public String CreateUser() throws Exception {
        return identityClient.PostUser();
    }

    public String CreateUser(String userName, String pwd, String emailAddress) throws Exception {
        return identityClient.PostUser(userName, pwd, emailAddress);
    }

    public List<String> GetUserByUserName(String userName) throws Exception{
        return identityClient.GetUserByUserName(userName);
    }

    public void postEmailVerification(String uid, String country, String locale) throws Exception {
        identityClient.PostEmailVerification(uid, country, locale);
    }

    public void creditWallet(String uid, EwalletInfo ewalletInfo, BigDecimal creditAmount) throws Exception {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCurrency(ewalletInfo.getCurrency().toString());
        creditRequest.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        ewalletInfo.setBalance(creditAmount);
        creditRequest.setAmount(creditAmount);
        paymentClient.creditWallet(creditRequest);
    }

    public void creditWallet(String uid, BigDecimal amount) throws Exception {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCurrency("usd");
        creditRequest.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        creditRequest.setAmount(amount);
        paymentClient.creditWallet(creditRequest);
    }

    public void creditWallet(String uid) throws Exception {
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCurrency("usd");
        creditRequest.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        creditRequest.setAmount(new BigDecimal(500));
        paymentClient.creditWallet(creditRequest);
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {

        PaymentInstrument paymentInstrument = new PaymentInstrument();
        ArrayList<Long> admins = new ArrayList<>();
        admins.add(IdConverter.hexStringToId(UserId.class, uid));
        paymentInstrument.setAdmins(admins);
        paymentInstrument.setLabel("4");
        TypeSpecificDetails typeSpecificDetails = new TypeSpecificDetails();
        Long billingAddressId = Master.getInstance().getUser(uid).getAddresses().get(0).getValue().getValue();
        paymentInfo.setBillingAddressId(billingAddressId);
        switch (paymentInfo.getType()) {
            case CREDITCARD:
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                typeSpecificDetails.setExpireDate(creditCardInfo.getExpireDate());
                typeSpecificDetails.setEncryptedCvmCode(creditCardInfo.getEncryptedCVMCode());
                paymentInstrument.setTypeSpecificDetails(typeSpecificDetails);
                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
                paymentInstrument.setIsValidated(creditCardInfo.isValidated());
                paymentInstrument.setType(creditCardInfo.getType().getValue());
                paymentInstrument.setBillingAddressId(creditCardInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            case EWALLET:
                EwalletInfo ewalletInfo = (EwalletInfo) paymentInfo;
                typeSpecificDetails.setStoredValueCurrency(ewalletInfo.getCurrency().toString());
                paymentInstrument.setTypeSpecificDetails(typeSpecificDetails);
                paymentInstrument.setAccountName(ewalletInfo.getAccountName());
                paymentInstrument.setType(ewalletInfo.getType().getValue());
                paymentInstrument.setIsValidated(ewalletInfo.isValidated());
                paymentInstrument.setBillingAddressId(billingAddressId);
                paymentInstrument.setBillingAddressId(ewalletInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            case PAYPAL:
                PayPalInfo payPalInfo = (PayPalInfo) paymentInfo;
                paymentInstrument.setAccountName(payPalInfo.getAccountName());
                paymentInstrument.setAccountNum(payPalInfo.getAccountNum());
                paymentInstrument.setIsValidated(payPalInfo.isValidated());
                paymentInstrument.setType(payPalInfo.getType().getValue());
                paymentInstrument.setBillingAddressId(payPalInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            case OTHERS:
                AdyenInfo adyenInfo = (AdyenInfo) paymentInfo;
                paymentInstrument.setAccountName(adyenInfo.getAccountName());
                paymentInstrument.setAccountNum(adyenInfo.getAccountNum());
                paymentInstrument.setIsValidated(adyenInfo.isValidated());
                paymentInstrument.setType(adyenInfo.getType().getValue());
                paymentInstrument.setBillingAddressId(adyenInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }

    }


    public String updatePaymentInstrument(String uid, String paymentId,
                                          PaymentInstrumentBase paymentInfo) throws Exception {
        PaymentInstrument paymentInstrument = Master.getInstance().getPaymentInstrument(paymentId);
        switch (paymentInfo.getType()) {
            case CREDITCARD:
                /*
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
                */
                return paymentClient.updatePaymentInstrument(uid, paymentId, paymentInstrument);
            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }

    }

    public String getPaymentInstrument(String paymentId) throws Exception {
        return paymentClient.getPaymentInstrumentByPaymentId(paymentId);
    }

    public List<String> getPaymentInstruments(String uid) throws Exception {
        return paymentClient.getPaymentInstrumentsByUserId(uid);
    }

    public void deletePaymentInstruments(String uid, String paymentId) throws Exception {
        paymentClient.deletePaymentInstrument(uid, paymentId);
    }

    public void invalidateCreditCard(String uid, String formattedPaymentId) throws Exception {
        String paymentId = IdConverter.hexStringToId(PaymentInstrumentId.class, formattedPaymentId).toString();
        String sqlStr = String.format(
                "update shard_%s.payment_instrument set external_token='123' where payment_instrument_id='%s'",
                ShardIdHelper.getShardIdByUid(uid), paymentId);
        dbHelper.executeUpdate(sqlStr, DBHelper.DBName.PAYMENT);
    }

}
