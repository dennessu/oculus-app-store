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
import com.junbo.common.id.*;
import com.junbo.store.spec.model.Address;
import com.junbo.store.spec.model.ChallengeAnswer;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.*;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.login.*;
import com.junbo.store.spec.model.purchase.*;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.payment.utility.PaymentTestDataProvider;
import com.junbo.test.store.apihelper.LoginService;
import com.junbo.test.store.apihelper.StoreService;
import com.junbo.test.store.apihelper.impl.LoginServiceImpl;
import com.junbo.test.store.apihelper.impl.StoreServiceImpl;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreTestDataProvider extends BaseTestDataProvider {

    LoginService loginClient = LoginServiceImpl.getInstance();
    StoreService storeClient = StoreServiceImpl.getInstance();
    OfferService offerClient = OfferServiceImpl.instance();
    OAuthService oAuthClient = OAuthServiceImpl.getInstance();

    PaymentTestDataProvider paymentProvider = new PaymentTestDataProvider();

    public CreateUserRequest CreateUserRequest() throws Exception {
       return CreateUserRequest(RandomFactory.getRandomStringOfAlphabet(6));
    }

    public CreateUserRequest CreateUserRequest(String username) throws Exception{
        CreateUserRequest createUserRequest = new CreateUserRequest();
        String dateString = "1990-01-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);
        createUserRequest.setDob(date);
        String emailAddress = RandomFactory.getRandomEmailAddress();
        createUserRequest.setEmail(emailAddress);
        createUserRequest.setFirstName(RandomFactory.getRandomStringOfAlphabet(5));
        createUserRequest.setLastName(RandomFactory.getRandomStringOfAlphabet(5));
        createUserRequest.setNickName(RandomFactory.getRandomStringOfAlphabet(4));
        createUserRequest.setPassword("Test1234");
        createUserRequest.setUsername(username);
        createUserRequest.setPin("1234");
        createUserRequest.setCor(Country.DEFAULT.toString());
        createUserRequest.setPreferredLocale("en_US");

        return createUserRequest;
    }

    public AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, boolean needVerifyEmail, int expectedResponseCode) throws Exception {
        AuthTokenResponse response = loginClient.CreateUser(createUserRequest, expectedResponseCode);

        if (needVerifyEmail && expectedResponseCode == 200) {
            oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
            List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(response.getUserId()), createUserRequest.getEmail());
            assert links != null;
            for(String link : links) {
                oAuthClient.accessEmailVerifyLink(link);
            }
        }

        return response;
    }

    public AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, boolean needVerifyEmail) throws Exception {
        return CreateUser(createUserRequest, needVerifyEmail, 200);
    }

    public UserNameCheckResponse CheckUserName(String userName) throws Exception {
        UserNameCheckRequest request = new UserNameCheckRequest();
        request.setUsername(userName);
        return loginClient.CheckUserName(request);
    }

    public UserNameCheckResponse CheckEmail(String email) throws Exception {
        UserNameCheckRequest request = new UserNameCheckRequest();
        request.setEmail(email);
        return loginClient.CheckUserName(request);
    }

    public AuthTokenResponse SignIn(String username, String password, int expectedCode) throws Exception {
        UserSignInRequest userSignInRequest = new UserSignInRequest();
        userSignInRequest.setUsername(username);
        UserCredential userCredential = new UserCredential();
        userCredential.setType("PASSWORD");
        userCredential.setValue(password);
        userSignInRequest.setUserCredential(userCredential);
        return loginClient.signIn(userSignInRequest, expectedCode);
    }

    public AuthTokenResponse SignIn(String userName, String password) throws Exception {
        return SignIn(userName, password, 200);
    }

    public UserCredentialRateResponse RateUserCredential(String password, String username) throws Exception {
        UserCredentialRateRequest userCredentialRateRequest = new UserCredentialRateRequest();
        UserCredential userCredential = new UserCredential();
        userCredential.setType("PASSWORD");
        userCredential.setValue(password);
        userCredentialRateRequest.setUserCredential(userCredential);

        if (!StringUtils.isEmpty(username)) {
            UserCredentialRateContext context = new UserCredentialRateContext();
            context.setUsername(username);
            userCredentialRateRequest.setContext(context);
        }
        return loginClient.rateUserCredential(userCredentialRateRequest);
    }

    public UserCredentialRateResponse RateUserCredential(String password) throws Exception {
        return RateUserCredential(password, null);
    }

    public InstrumentUpdateResponse CreateCreditCard(String uid) throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = new Instrument();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String encryptedString = paymentProvider.encryptCreditCardInfo(creditCardInfo);
        instrument.setAccountName(creditCardInfo.getAccountName());
        instrument.setAccountNum(encryptedString);
        instrument.setBillingAddress(getBillingAddress());
        instrument.setType("CREDITCARD");
        instrument.setIsDefault(false);
        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest);
    }

    public InstrumentUpdateResponse UpdateCreditCard(InstrumentUpdateResponse response, boolean isDefault) throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = response.getBillingProfile().getInstruments().get(1);
        instrument.setIsDefault(isDefault);
        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest);
    }

    public InstrumentUpdateResponse CreateStoredValue() throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = new Instrument();
        //instrument.setBillingAddress(getBillingAddress());
        instrument.setType("STOREDVALUE");
        instrument.setStoredValueCurrency("USD");

        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest);
    }

    public void CreditStoredValue(String uid, BigDecimal amount) throws Exception {
        paymentProvider.creditWallet(uid, amount);
    }

    public PreparePurchaseResponse preparePurchase(String token, String offerId, PaymentInstrumentId piid, String pin, TosId tosAcceptanceId) throws Exception {
        PreparePurchaseRequest request = new PreparePurchaseRequest();
        request.setPurchaseToken(token);
        request.setInstrument(piid);
        request.setOffer(new OfferId(offerId));

        if (!StringUtils.isEmpty(pin)) {
            ChallengeAnswer challengeAnswer = new ChallengeAnswer();
            challengeAnswer.setType("PIN");
            challengeAnswer.setPin(pin);
            request.setChallengeAnswer(challengeAnswer);
        }
        if (tosAcceptanceId != null) {
            ChallengeAnswer challengeAnswer = new ChallengeAnswer();
            challengeAnswer.setType("TOS_ACCEPTANCE");
            challengeAnswer.setAcceptedTos(tosAcceptanceId);
            request.setChallengeAnswer(challengeAnswer);
        }
        return storeClient.preparePurchase(request);
    }

    public String getOfferIdByName(String offerName) throws Exception {
        return offerClient.getOfferIdByName(offerName);
    }

    public CommitPurchaseResponse commitPurchase(String uid, String purchaseToken) throws Exception {
        CommitPurchaseRequest commitPurchaseRequest = new CommitPurchaseRequest();
        commitPurchaseRequest.setPurchaseToken(purchaseToken);
        //commitPurchaseRequest.setChallengeSolution();
        return storeClient.commitPurchase(commitPurchaseRequest);
    }

    public IAPEntitlementConsumeResponse iapConsumeEntitlement(EntitlementId entitlementId, String offerId) throws Exception {
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

    public MakeFreePurchaseResponse makeFreePurchase(String offerId, TosId tosId) throws Exception {
        MakeFreePurchaseRequest request = new MakeFreePurchaseRequest();
        request.setOffer(new OfferId(offerId));
        if (tosId != null) {
            ChallengeAnswer challengeAnswer = new ChallengeAnswer();
            challengeAnswer.setType("TOS_ACCEPTANCE");
            challengeAnswer.setAcceptedTos(tosId);
            request.setChallengeAnswer(challengeAnswer);
        }
        return storeClient.makeFreePurchase(request);
    }

    public DetailsResponse getItemDetails(String itemId) throws Exception {
        DetailsRequest request = new DetailsRequest();
        request.setItemId(new ItemId(itemId));
        return storeClient.getDetails(request);
    }

    public AuthTokenResponse signIn(String userName) throws Exception {
        UserSignInRequest request = new UserSignInRequest();
        request.setUsername(userName);
        UserCredential userCredential = new UserCredential();
        userCredential.setType("password");
        userCredential.setValue("Test1234");
        request.setUserCredential(userCredential);
        return loginClient.signIn(request);
    }

    public UserProfileGetResponse getUserProfile() throws Exception {
        return getUserProfile(200);
    }

    public UserProfileGetResponse getUserProfile(int expectedResponseCode) throws Exception {
        return storeClient.getUserProfile(expectedResponseCode);
    }

    public UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest, int expectedResponseCode) throws Exception {
        return storeClient.updateUserProfile(userProfileUpdateRequest, expectedResponseCode);
    }

    public UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest) throws Exception {
        return updateUserProfile(userProfileUpdateRequest, 200);
    }

    public VerifyEmailResponse verifyEmail(VerifyEmailRequest verifyEmailRequest) throws Exception {
        return verifyEmail(verifyEmailRequest, 200);
    }

    public VerifyEmailResponse verifyEmail(VerifyEmailRequest verifyEmailRequest, int exceptedResponseCode) throws Exception {
        return storeClient.verifyEmail(verifyEmailRequest, exceptedResponseCode);
    }

    public EntitlementsGetResponse getEntitlement() throws Exception {
        return storeClient.getEntitlement();
    }

    public AuthTokenResponse getToken(String refreshToken, int expectedCode) throws Exception {
        AuthTokenRequest request = new AuthTokenRequest();
        request.setRefreshToken(refreshToken);
        return loginClient.getToken(request, expectedCode);
    }

    public AuthTokenResponse getToken(String refreshToken) throws Exception {
        return getToken(refreshToken, 200);
    }

    public BillingProfileGetResponse getBillingProfile(String offerId) throws Exception{
        BillingProfileGetRequest request = new BillingProfileGetRequest();
        request.setOffer(offerId == null ? null : new OfferId(offerId));
        return storeClient.getBillingProfile(request);
    }

    public LibraryResponse getLibrary() throws Exception {
        return storeClient.getLibrary();
    }

    public TocResponse getToc() throws Exception {
        return storeClient.getTOC();
    }

    public ListResponse getList(ListRequest request) throws Exception {
        return storeClient.getList(request);
    }

    public SectionLayoutResponse getLayout(String category, String criteria, Integer count) throws Exception {
        SectionLayoutRequest request = new SectionLayoutRequest();
        request.setCategory(category);
        request.setCriteria(criteria);
        request.setCount(count);
        return storeClient.getSectionLayout(request);
    }

    public AcceptTosResponse acceptTos(TosId tosId) throws Exception {
        AcceptTosRequest request = new AcceptTosRequest();
        request.setTosId(tosId);
        return storeClient.acceptTos(request);
    }

    public DeliveryResponse getDelivery(ItemId itemId) throws Exception {
        DeliveryRequest request = new DeliveryRequest();
        request.setItemId(itemId);
        return storeClient.getDelivery(request);
    }
}
