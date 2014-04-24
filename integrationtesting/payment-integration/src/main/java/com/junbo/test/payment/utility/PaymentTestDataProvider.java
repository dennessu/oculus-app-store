/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.payment.utility;

import com.junbo.common.id.UserId;
import com.junbo.payment.spec.model.Address;
import com.junbo.payment.spec.model.CreditCardRequest;
import com.junbo.payment.spec.model.PaymentInstrument;
import com.junbo.payment.spec.model.WalletRequest;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.EwalletInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.payment.apihelper.PaymentService;
import com.junbo.test.payment.apihelper.impl.PaymentServiceImpl;

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

    public String postPaymentInstrument(String uid, PaymentInstrumentBase paymentInfo) throws Exception {
        PaymentInstrument paymentInstrument = new PaymentInstrument();
        Address address = new Address();
        paymentInstrument.setPhoneNum("650-253-0000");
        paymentInstrument.setTrackingUuid(UUID.randomUUID());
        ArrayList<Long> admins = new ArrayList<>();
        admins.add(IdConverter.hexStringToId(UserId.class, uid));
        paymentInstrument.setAdmins(admins);

        switch (paymentInfo.getType()) {
            case CREDITCARD:
                CreditCardInfo creditCardInfo = (CreditCardInfo) paymentInfo;
                CreditCardRequest creditCardRequest = new CreditCardRequest();
                //creditCardRequest.setType(creditCardInfo.getType().toString());
                creditCardRequest.setExpireDate(creditCardInfo.getExpireDate());
                creditCardRequest.setEncryptedCvmCode(creditCardInfo.getEncryptedCVMCode());

                address.setAddressLine1(creditCardInfo.getAddress().getAddressLine1());
                address.setCity(creditCardInfo.getAddress().getCity());
                address.setState(creditCardInfo.getAddress().getState());
                address.setCountry(creditCardInfo.getAddress().getCountry());
                address.setPostalCode(creditCardInfo.getAddress().getPostalCode());

                paymentInstrument.setAccountName(creditCardInfo.getAccountName());
                paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
                //paymentInstrument.setAccountNum(creditCardInfo.getAccountNum());
                paymentInstrument.setAddress(address);
                paymentInstrument.setCreditCardRequest(creditCardRequest);
                paymentInstrument.setIsValidated(creditCardInfo.isValidated());
                //paymentInstrument.setIsDefault(String.valueOf(creditCardInfo.isDefault()));
                paymentInstrument.setType(creditCardInfo.getType().toString());

                return paymentClient.postPaymentInstrument(paymentInstrument);

            case EWALLET:
                EwalletInfo ewalletInfo = (EwalletInfo) paymentInfo;
                WalletRequest walletRequest = new WalletRequest();
                walletRequest.setType(ewalletInfo.getWalletType());
                walletRequest.setCurrency(ewalletInfo.getCurrency().toString());
                paymentInstrument.setWalletRequest(walletRequest);
                paymentInstrument.setAccountName(ewalletInfo.getAccountName());
                paymentInstrument.setType(ewalletInfo.getType().toString());
                paymentInstrument.setIsValidated(ewalletInfo.isValidated());

                address.setAddressLine1(ewalletInfo.getAddress().getAddressLine1());
                address.setCity(ewalletInfo.getAddress().getCity());
                address.setState(ewalletInfo.getAddress().getState());
                address.setCountry(ewalletInfo.getAddress().getCountry());
                address.setPostalCode(ewalletInfo.getAddress().getPostalCode());
                paymentInstrument.setAddress(address);

                return paymentClient.postPaymentInstrument(paymentInstrument);

            default:
                throw new TestException(String.format("%s is not supported", paymentInfo.getType().toString()));
        }

    }


    public String updatePaymentInstrument(String uid, String paymentId,
                                          PaymentInstrumentBase paymentInfo) throws Exception {
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

    }

    public String getPaymentInstrument(String uid, String paymentId) throws Exception {
        return paymentClient.getPaymentInstrumentByPaymentId(uid, paymentId);
    }

    public List<String> searchPaymentInstruments(String uid) throws Exception {
        return paymentClient.searchPaymentInstrumentsByUserId(uid);
    }

    public void deletePaymentInstruments(String uid, String paymentId) throws Exception {
        paymentClient.deletePaymentInstrument(uid, paymentId);
    }

}
