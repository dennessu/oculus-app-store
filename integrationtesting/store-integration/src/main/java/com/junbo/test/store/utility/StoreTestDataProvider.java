/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.enumid.CountryId;
import com.junbo.common.enumid.LocaleId;
import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.common.id.UserId;
import com.junbo.store.spec.model.Address;
import com.junbo.store.spec.model.ChallengeAnswer;
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.billing.Instrument;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.login.*;
import com.junbo.store.spec.model.purchase.*;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Entities.enums.Country;
//import com.junbo.test.common.Entities.enums.Currency;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.payment.utility.PaymentTestDataProvider;
import com.junbo.test.store.apihelper.LoginService;
import com.junbo.test.store.apihelper.StoreService;
import com.junbo.test.store.apihelper.impl.LoginServiceImpl;
import com.junbo.test.store.apihelper.impl.StoreServiceImpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

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
        createUserRequest.setPin("1234");
        createUserRequest.setCor(Country.DEFAULT.toString());
        createUserRequest.setPreferredLocale("en_US");

        return loginClient.CreateUser(createUserRequest);
    }

    public AuthTokenResponse CreateUser(String userName) throws Exception {
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
        createUserRequest.setUsername(userName);
        createUserRequest.setPin("1234");
        createUserRequest.setCor(Country.DEFAULT.toString());
        createUserRequest.setPreferredLocale("en_US");

        return loginClient.CreateUser(createUserRequest);
    }

    public UserNameCheckResponse CheckUserName(String userName) throws Exception {
        UserNameCheckRequest request = new UserNameCheckRequest();
        request.setUsername(userName);
        return loginClient.CheckUserName(request);
    }

    public AuthTokenResponse SignIn(String userName, String password) throws Exception {
        UserSignInRequest userSignInRequest = new UserSignInRequest();
        userSignInRequest.setUsername(userName);
        UserCredential userCredential = new UserCredential();
        userCredential.setType("PASSWORD");
        userCredential.setValue(password);
        userSignInRequest.setUserCredential(userCredential);
        return loginClient.signIn(userSignInRequest);
    }

    public UserCredentialRateResponse RateUserCredential(String password) throws Exception {
        UserCredentialRateRequest userCredentialRateRequest = new UserCredentialRateRequest();
        UserCredential userCredential = new UserCredential();
        userCredential.setType("PASSWORD");
        userCredential.setValue(password);
        userCredentialRateRequest.setUserCredential(userCredential);
        return loginClient.rateUserCredential(userCredentialRateRequest);
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

    public BillingProfileUpdateResponse CreateStoredValue(String uid) throws Exception {
        BillingProfileUpdateRequest billingProfileUpdateRequest = new BillingProfileUpdateRequest();
        Instrument instrument = new Instrument();
        instrument.setBillingAddress(getBillingAddress());
        instrument.setType("STOREDVALUE");
        instrument.setStoredValueCurrency("USD");

        billingProfileUpdateRequest.setInstrument(instrument);
        billingProfileUpdateRequest.setAction("ADD_PI");
        billingProfileUpdateRequest.setUserId(new UserId(IdConverter.hexStringToId(UserId.class, uid)));
        billingProfileUpdateRequest.setLocale(new LocaleId("en_US"));
        return storeClient.updateBillingProfile(billingProfileUpdateRequest);
    }

    public void CreditStoredValue(String uid, BigDecimal amount) throws Exception {
        paymentProvider.creditWallet(uid, amount);
    }

    public PreparePurchaseResponse preparePurchase(String token, String offerId, PaymentInstrumentId piid, String pin) throws Exception {
        PreparePurchaseRequest request = new PreparePurchaseRequest();
        request.setLocale(new LocaleId("en_US"));
        request.setCountry(new CountryId(Country.DEFAULT.toString()));
        request.setPurchaseToken(token);
        request.setInstrument(piid);
        request.setOffer(new OfferId(offerId));

        ChallengeAnswer challengeAnswer = new ChallengeAnswer();
        challengeAnswer.setType("PIN");
        challengeAnswer.setPin(pin);
        //request.setIapParams();
        return storeClient.preparePurchase(request);
    }

    public String getOfferIdByName(String offerName)throws Exception{
        return offerClient.getOfferIdByName(offerName);
    }

    public CommitPurchaseResponse commitPurchase(String uid, String purchaseToken) throws Exception {
        CommitPurchaseRequest commitPurchaseRequest = new CommitPurchaseRequest();
        commitPurchaseRequest.setPurchaseToken(purchaseToken);
        //commitPurchaseRequest.setChallengeSolution();
        return storeClient.commitPurchase(commitPurchaseRequest);
    }

    public IAPEntitlementConsumeResponse iapConsumeEntitlement(EntitlementId entitlementId, String offerId) throws Exception{
        IAPEntitlementConsumeRequest request = new IAPEntitlementConsumeRequest();
        request.setTrackingGuid(UUID.randomUUID().toString());
        request.setEntitlement(entitlementId);
        request.setUseCountConsumed(1);
        Offer offer = Master.getInstance().getOffer(offerId);
        OfferRevision offerRevision = Master.getInstance().getOfferRevision(offer.getCurrentRevisionId());
        Item item = Master.getInstance().getItem(offerRevision.getItems().get(0).getItemId());
        ItemRevision itemRevision = Master.getInstance().getItemRevision(item.getCurrentRevisionId());
        String packageName = itemRevision.getPackageName();
        request.setPackageName(packageName);
        return storeClient.iapConsumeEntitlement(request);
    }

    private Address getBillingAddress() throws Exception {
        Address address = new Address();
        address.setStreet1("800 West Campbell Road");
        address.setCity("Richardson");
        address.setPostalCode("75080");
        address.setSubCountry("TX");
        address.setCountry("US");
        return address;
    }

    public MakeFreePurchaseResponse makeFreePurchase(String offerId, Country country) throws Exception{
        MakeFreePurchaseRequest request = new MakeFreePurchaseRequest();
        request.setLocale(new LocaleId("en_US"));
        request.setCountry(new CountryId(country.toString()));
        request.setOffer(new OfferId(offerId));
        return storeClient.makeFreePurchase(request);
    }

}
