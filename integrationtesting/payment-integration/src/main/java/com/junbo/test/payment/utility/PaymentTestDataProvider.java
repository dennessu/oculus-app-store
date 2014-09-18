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
import com.junbo.test.payment.apihelper.clientencryption.Card;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.math.BigDecimal;
import java.util.Date;
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

    public List<String> GetUserByUserName(String userName) throws Exception {
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
        return postPaymentInstrument(uid, paymentInfo, 200);
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo,
                                        long billingAddressId) throws Exception {
        return postPaymentInstrument(uid, paymentInfo, billingAddressId, 200);
    }

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo,
                                        long billingAddressId, int expectedResponseCode) throws Exception {

        PaymentInstrument paymentInstrument = new PaymentInstrument();
        paymentInstrument.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        paymentInstrument.setLabel("4");
        TypeSpecificDetails typeSpecificDetails = new TypeSpecificDetails();
        if (billingAddressId <= 0) {
            billingAddressId = Master.getInstance().getUser(uid).getAddresses().get(0).getValue().getValue();
        }
        paymentInfo.setBillingAddressId(billingAddressId);
        switch (paymentInfo.getType()) {
            case CREDITCARD:
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAccountNumber(encryptCreditCardInfo(creditCardInfo));
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
                paymentInstrument.setAccountNumber(payPalInfo.getAccountNum());
                paymentInstrument.setIsValidated(payPalInfo.isValidated());
                paymentInstrument.setType(payPalInfo.getType().getValue());
                paymentInstrument.setBillingAddressId(payPalInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument));
                return paymentInfo.getPid();

            case OTHERS:
                AdyenInfo adyenInfo = (AdyenInfo) paymentInfo;
                paymentInstrument.setAccountName(adyenInfo.getAccountName());
                paymentInstrument.setAccountNumber(adyenInfo.getAccountNum());
                paymentInstrument.setIsValidated(adyenInfo.isValidated());
                paymentInstrument.setType(adyenInfo.getType().getValue());
                paymentInstrument.setBillingAddressId(adyenInfo.getBillingAddressId());

                paymentInfo.setPid(paymentClient.postPaymentInstrument(paymentInstrument, expectedResponseCode));
                return paymentInfo.getPid();

            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }

    }


    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo,
                                        int expectedResponseCode) throws Exception {
        return postPaymentInstrument(uid, paymentInfo, 0L, expectedResponseCode);
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

    public String getPaymentInstrument(String paymentId, int expectedResponseCode) throws Exception {
        return paymentClient.getPaymentInstrumentByPaymentId(paymentId, expectedResponseCode);
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

    public String getEwalletBalanceFromDB(String uid) throws Exception {
        String sqlStr = String.format(
                "select balance from shard_%s.ewallet where user_id = '%s'",
                ShardIdHelper.getShardIdByUid(uid), IdConverter.hexStringToId(UserId.class, uid));
        return dbHelper.executeScalar(sqlStr, DBHelper.DBName.EWALLET);
    }


    public String encryptCreditCardInfo(CreditCardInfo creditCardInfo) {

        Card card = new Card.Builder(new Date())
                .number(creditCardInfo.getAccountNum())
                .cvc(creditCardInfo.getEncryptedCVMCode())
                .expiryMonth("06")
                .expiryYear("2020")
                .holderName(creditCardInfo.getAccountNum())
                .build();

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");

        String jsFileName = "AdyenEncrypt.js";

        try {
            String script = readFileContent(jsFileName);
            engine.eval(script);
            if (engine instanceof Invocable) {
                Invocable invoke = (Invocable) engine;
                return (String) invoke.invokeFunction("encryptcc", card.toString());
            }
        } catch (Exception e) {
            throw new TestException(e.getMessage());
        }

        return null;
    }


    /*
    public String encryptCreditCardInfo(CreditCardInfo creditCardInfo) throws EncrypterException {
        Encrypter e = new Encrypter(pubKey);

        Card card = new Card.Builder(new Date())
                .number(creditCardInfo.getAccountNum())
                .cvc(creditCardInfo.getEncryptedCVMCode())
                .expiryMonth("06")
                .expiryYear("2020")
                .holderName(creditCardInfo.getAccountNum())
                .build();

        return e.encrypt(card.toString());

    }
    */


    String pubKey = "10001|9699D59B070DBA71B53A696C67B8FB8538C5C9B73D2BF485104858"
            + "DD12BC7D706A096DE6D8508175311A5B15EABE829C51DF269228EC75C8B3"
            + "35938C91A9E0B624A59734EBC77F97E22A3BA2A16FE9B185D9824EFC9597"
            + "AC3BE7E6D198E2A64D35FB2685EBA3B148C8CDD49B27BDF50AD7B65B4F20"
            + "43E459BB18748E91E942D3FCD6DFA6AD9E4E0F35289C77E5CE15621FBA2E"
            + "759F47AE7DBE1B86355BA828F97F23D736CF7FBB582A1DABA75528C58D83"
            + "413848AE07DAEBB98EF714B2749A64E6F2B30F47E8731E9F6AB0618D65F0"
            + "014098FAE42F7E475BD6A7E23D33DC6F0D363DEBCA613007EE06C6B3371B"
            + "03E5FEB0745F31F5BBFD95D6E4ADC403E161BF";


}
