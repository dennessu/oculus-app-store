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
import com.junbo.test.payment.apihelper.clientencryption.Encrypter;
import com.junbo.test.payment.apihelper.clientencryption.EncrypterException;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

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
                                        int expectedResponseCode) throws Exception {

        PaymentInstrument paymentInstrument = new PaymentInstrument();
        //ArrayList<Long> admins = new ArrayList<>();
        //admins.add(IdConverter.hexStringToId(UserId.class, uid));
        //paymentInstrument.setAdmins(admins);
        paymentInstrument.setUserId(IdConverter.hexStringToId(UserId.class, uid));
        paymentInstrument.setLabel("4");
        TypeSpecificDetails typeSpecificDetails = new TypeSpecificDetails();
        Long billingAddressId = Master.getInstance().getUser(uid).getAddresses().get(0).getValue().getValue();
        paymentInfo.setBillingAddressId(billingAddressId);
        switch (paymentInfo.getType()) {
            case CREDITCARD:
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                //GregorianCalendar gc = new GregorianCalendar();
                // paymentInstrument.setLastValidatedTime(gc.getTime());
                // paymentInstrument.setTypeSpecificDetails(typeSpecificDetails);
                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAccountNumber(accountNum);
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

    String pubKey = "10001|80C7821C961865FB4AD23F172E220F819A5CC7B9956BC3458E2788"
            + "F9D725B07536E297B89243081916AAF29E26B7624453FC84CB10FC7DF386"
            + "31B3FA0C2C01765D884B0DA90145FCE217335BCDCE4771E30E6E5630E797"
            + "EE289D3A712F93C676994D2746CBCD0BEDD6D29618AF45FA6230C1D41FE1"
            + "DB0193B8FA6613F1BD145EA339DAC449603096A40DC4BF8FACD84A5D2CA5"
            + "ECFC59B90B928F31715A7034E7B674E221F1EB1D696CC8B734DF7DE2E309"
            + "E6E8CF94156686558522629E8AF59620CBDE58327E9D84F29965E4CD0FAF"
            + "A38C632B244287EA1F7F70DAA445D81C216D3286B09205F6650262CAB415"
            + "5F024B3294A933F4DC514DE0B5686F6C2A6A2D";

    String accountNum = "adyenjs_0_1_4$O2eavEUHb6lQeQPkEOHQGfOIe22JTUkuCj+iwuR07OJtjlV7ZFwV5dhEVVVHOt60JMncweUsDOn"
            + "N5UkkH6vFneVvFZ31T0TaLH6VixXnexTwFqr/qVZu4N3lPNgkGM8KmL/PMOsUnV8kDAbu3ilh4P+6y3MsWjB5is4X/n63zfnIO4R"
            + "eiOEVaAAio1UGj7nh1o3q50oRXBjg/zE4/vWylTabsW+S6SoqYvQQCO8NtUSeSXQDp2bMxUu89bzjvaGJKNPzZLjGxFz68m20nXu"
            + "6RQlEsfSpaqXQB3/rC3O/ycCNrpZZc1PM6wqp8WbgHDcSdYpc3SaN8zYCKU1QBM5giA==$rictoeF32VwhKudtbw81i/xci/5cGL" +
            "8DcQqap7mvXxZxVC8EBXuzjLeIEQvWREM+Uk65/OARub1dqdGemzbiSo6+9nGZ5OCUdZ2nAE6Et4eav30/ZFgpRfJwiP+mr7cwMtNQ" +
            "843CFqaChoTcV1n6gcFXNopUkiWc/V4cvWJCjL/BUSKf/57W3CcvZJvS+y68";

}
