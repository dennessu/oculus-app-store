/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.CurrencyId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.billing.Instrument;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.identity.PersonalInfo;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.purchase.*;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.payment.utility.PaymentTestDataProvider;
import com.junbo.test.store.apihelper.LoginService;
import com.junbo.test.store.apihelper.StoreService;
import com.junbo.test.store.apihelper.impl.LoginServiceImpl;
import com.junbo.test.store.apihelper.impl.StoreServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreTestDataProvider extends BaseTestDataProvider {

    LoginService loginClient = LoginServiceImpl.getInstance();
    StoreService storeClient = StoreServiceImpl.getInstance();
    OfferService offerClient = OfferServiceImpl.instance();

    PaymentTestDataProvider paymentProvider = new PaymentTestDataProvider();

    public AuthTokenResponse CreateUser() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        String dateString = "1990-01-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);
        createUserRequest.setDob(date);
        createUserRequest.setEmail(RandomFactory.getRandomEmailAddress());
        createUserRequest.setFirstName(RandomFactory.getRandomStringOfAlphabet(5));
        createUserRequest.setLastName(RandomFactory.getRandomStringOfAlphabet(5));
        createUserRequest.setNickName(RandomFactory.getRandomStringOfAlphabet(4));
        createUserRequest.setPassword("Test1234");
        createUserRequest.setUsername(RandomFactory.getRandomStringOfAlphabet(6));
        createUserRequest.setPinCode(RandomFactory.getRandomStringOfNumeric(4));
        createUserRequest.setCor(Country.DEFAULT.toString());
        createUserRequest.setPreferredLocale("en_US");

        return loginClient.CreateUser(createUserRequest);
    }

    public BillingProfileUpdateResponse CreateCreditCard(String uid) throws Exception {
        BillingProfileUpdateRequest billingProfileUpdateRequest = new BillingProfileUpdateRequest();
        Instrument instrument = new Instrument();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String encryptedString = paymentProvider.encryptCreditCardInfo(creditCardInfo);
        instrument.setAccountName(creditCardInfo.getAccountName());
        instrument.setAccountNum(encryptedString);
        instrument.setBillingAddress(getBillingAddress());
        instrument.setType("CREDITCARD");
        billingProfileUpdateRequest.setInstrument(instrument);
        billingProfileUpdateRequest.setAction("ADD_PI");
        billingProfileUpdateRequest.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        billingProfileUpdateRequest.setLocale(new LocaleId("en_US"));
        return storeClient.updateBillingProfile(billingProfileUpdateRequest);
    }

    public PreparePurchaseResponse preparePurchase(String uid, String offerName) throws Exception {
        PreparePurchaseRequest request = new PreparePurchaseRequest();
        request.setLocale(new LocaleId("en_US"));
        request.setCountry(new CountryId(Country.DEFAULT.toString()));
        request.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        request.setCurrency(new CurrencyId(Currency.DEFAULT.toString()));
        request.setOfferId(new OfferId(offerClient.getOfferIdByName(offerName)));
        //request.setIapParams();
        return storeClient.preparePurchase(request);
    }

    public SelectInstrumentResponse selectInstrument(String uid, String purchaseToken, long paymentId) throws Exception{
        SelectInstrumentRequest selectInstrumentRequest = new SelectInstrumentRequest();
        selectInstrumentRequest.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        selectInstrumentRequest.setPurchaseToken(purchaseToken);
        selectInstrumentRequest.setInstrumentId(new PaymentInstrumentId(paymentId));
        return storeClient.selectInstrumentForPurchase(selectInstrumentRequest);
    }

    public CommitPurchaseResponse commitPurchase(String uid, String purchaseToken) throws Exception {
        CommitPurchaseRequest commitPurchaseRequest = new CommitPurchaseRequest();
        commitPurchaseRequest.setPurchaseToken(purchaseToken);
        commitPurchaseRequest.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        //commitPurchaseRequest.setChallengeSolution();
        return storeClient.commitPurchase(commitPurchaseRequest);
    }

    public IAPEntitlementConsumeResponse iapConsumeEntitlement(String uid, EntitlementId entitlementId) throws Exception{
        IAPEntitlementConsumeRequest request = new IAPEntitlementConsumeRequest();
        request.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        request.setEntitlementId(entitlementId);
        request.setUseCountConsumed(1);
        return storeClient.iapConsumeEntitlement(request);
    }

    private PersonalInfo getBillingAddress() throws Exception {
        PersonalInfo personalInfo = new PersonalInfo();
        personalInfo.setType("ADDRESS");
        String str = "{\"subCountry\":\"TX\",\"street1\":\"800 West Campbell Road\"," +
                "\"city\":\"Richardson\",\"postalCode\":\"75080\"," +
                "\"country\":{\"id\":\"US\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);
        personalInfo.setValue(value);
        return personalInfo;
    }

}
