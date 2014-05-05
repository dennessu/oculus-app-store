/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.utility;

import com.junbo.common.id.UserId;
import com.junbo.ewallet.spec.model.CreditRequest;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.TypeSpecificDetails;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
//import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.payment.apihelper.PaymentService;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public void creditWallet(String uid, EwalletInfo ewalletInfo, BigDecimal creditAmount) throws Exception{
        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setCurrency("usd");
        creditRequest.setUserId(IdConverter.hexStringToId(UserId.class,uid));
        ewalletInfo.setBalance(creditAmount);
        creditRequest.setAmount(creditAmount);
        paymentClient.creditWallet(creditRequest);
    }


    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {

        PaymentInstrument paymentInstrument = new PaymentInstrument();
        Address address = new Address();
        paymentInstrument.setTrackingUuid(UUID.randomUUID());
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

                return paymentClient.postPaymentInstrument(paymentInstrument);

            case EWALLET:
                EwalletInfo ewalletInfo = (EwalletInfo) paymentInfo;
                typeSpecificDetails.setStoredValueCurrency("usd");
                paymentInstrument.setTypeSpecificDetails(typeSpecificDetails);
                paymentInstrument.setAccountName(ewalletInfo.getAccountName());
                paymentInstrument.setType(ewalletInfo.getType().getValue());
                paymentInstrument.setIsValidated(ewalletInfo.isValidated());
                paymentInstrument.setBillingAddressId(billingAddressId);
                address.setAddressLine1(ewalletInfo.getAddress().getAddressLine1());
                address.setCity(ewalletInfo.getAddress().getCity());
                address.setState(ewalletInfo.getAddress().getState());
                address.setCountry(ewalletInfo.getAddress().getCountry());
                address.setPostalCode(ewalletInfo.getAddress().getPostalCode());
                paymentInstrument.setBillingAddressId(ewalletInfo.getBillingAddressId());
                paymentInstrument.setAddress(address);

                return paymentClient.postPaymentInstrument(paymentInstrument);

            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }

    }


    public String updatePaymentInstrument(String uid, String paymentId,
                                          PaymentInstrumentBase paymentInfo) throws Exception {
        /*
        switch (paymentInfo.getType()) {
            case CREDITCARD:
                PaymentInstrument paymentInstrument = Master.getInstance().getPaymentInstrument(paymentId);
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                CreditCardRequest creditCardRequest = paymentInstrument.getCreditCardRequest();
                //creditCardRequest.setType(creditCardInfo.getType().toString());
                creditCardRequest.setExpireDate(creditCardInfo.getExpireDate());
                creditCardRequest.setEncryptedCvmCode(creditCardInfo.getEncryptedCVMCode());

                Address address = new Address();
                address.setAddressLine1(creditCardInfo.getAddress().getAddressLine1());
                address.setCity(creditCardInfo.getAddress().getCity());
                address.setState(creditCardInfo.getAddress().getState());
                address.setCountry(creditCardInfo.getAddress().getCountry());
                address.setPostalCode(creditCardInfo.getAddress().getPostalCode());

                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAddress(address);
                paymentInstrument.setCreditCardRequest(creditCardRequest);
                paymentInstrument.setIsValidated(creditCardInfo.isValidated());
                paymentInstrument.setPhoneNum("650-253-0000");
                //paymentInstrument.setIsDefault(String.valueOf(creditCardInfo.isDefault()));
                paymentInstrument.setType(creditCardInfo.getType().toString());

                return paymentClient.updatePaymentInstrument(uid, paymentId, paymentInstrument);
            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }
        */
        return null;

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

}
