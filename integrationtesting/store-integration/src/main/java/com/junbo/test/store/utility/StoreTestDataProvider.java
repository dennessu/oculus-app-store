/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.utility;

// CHECKSTYLE:OFF


import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.error.Error;
import com.junbo.common.id.*;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.emulator.casey.spec.model.CaseyEmulatorData;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker;
import com.junbo.order.spec.model.Order;
import com.junbo.store.spec.model.Address;
import com.junbo.store.spec.model.ChallengeAnswer;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.*;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;
import com.junbo.store.spec.model.external.casey.cms.CmsSchedule;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.login.*;
import com.junbo.store.spec.model.purchase.*;
import com.junbo.test.catalog.*;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.common.Entities.Identity.UserInfo;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseTestDataProvider;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.apihelper.order.OrderService;
import com.junbo.test.common.apihelper.order.impl.OrderServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.payment.utility.PaymentTestDataProvider;
import com.junbo.test.store.apihelper.CaseyEmulatorService;
import com.junbo.test.store.apihelper.LoginService;
import com.junbo.test.store.apihelper.StoreConfigService;
import com.junbo.test.store.apihelper.StoreService;
import com.junbo.test.store.apihelper.impl.CaseyEmulatorServiceImpl;
import com.junbo.test.store.apihelper.impl.LoginServiceImpl;
import com.junbo.test.store.apihelper.impl.StoreConfigServiceImpl;
import com.junbo.test.store.apihelper.impl.StoreServiceImpl;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by weiyu_000 on 8/6/14.
 */

public class StoreTestDataProvider extends BaseTestDataProvider {


    LoginService loginClient = LoginServiceImpl.getInstance();
    StoreService storeClient = StoreServiceImpl.getInstance();
    OfferService offerClient = OfferServiceImpl.instance();
    OfferRevisionService offerRevisionClient = OfferRevisionServiceImpl.instance();
    ItemService itemClient = ItemServiceImpl.instance();
    ItemRevisionService itemRevisionClient = ItemRevisionServiceImpl.instance();
    OAuthService oAuthClient = OAuthServiceImpl.getInstance();
    CaseyEmulatorService caseyEmulatorClient = CaseyEmulatorServiceImpl.getInstance();
    StoreConfigService storeConfigService = StoreConfigServiceImpl.getInstance();
    UserService identityClient = UserServiceImpl.instance();
    OfferAttributeService offerAttributeClient = OfferAttributeServiceImpl.instance();
    ItemAttributeService itemAttributeClient = ItemAttributeServiceImpl.instance();
    OrganizationService organizationClient = OrganizationServiceImpl.instance();
    OrderService orderClient = OrderServiceImpl.getInstance();

    PaymentTestDataProvider paymentProvider = new PaymentTestDataProvider();

    public CreateUserRequest CreateUserRequest() throws Exception {
        return CreateUserRequest(RandomFactory.getRandomStringOfAlphabet(6));
    }

    public CreateUserRequest CreateUserRequest(String username) throws Exception {
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

    public AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, boolean needVerifyEmail,
                                        int expectedResponseCode) throws Exception {
        AuthTokenResponse response = loginClient.CreateUser(createUserRequest, expectedResponseCode);

        if (needVerifyEmail && expectedResponseCode == 200) {
            oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
            List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(response.getUserId()),
                    createUserRequest.getEmail());
            assert links != null;
            for (String link : links) {
                confirmEmail(link);
                //oAuthClient.accessEmailVerifyLink(link);
            }
        }

        return response;
    }

    public void PrepareUsernameEmailBlocker(String username, String email) throws Exception {
        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY_MIGRATION);
        UsernameMailBlocker usernameMailBlocker = new UsernameMailBlocker();
        usernameMailBlocker.setEmail(email);
        usernameMailBlocker.setUsername(username);

        identityClient.postUsernameEmailBlocker(usernameMailBlocker);
    }

    public ConfirmEmailResponse confirmEmail(String link) throws Exception {
        ConfirmEmailRequest request = new ConfirmEmailRequest();
        request.setEvc(getEvcCode(link));
        return loginClient.confirmEmail(request, 200);
    }

    private String getEvcCode(String link) {
        int beginIndex = link.indexOf("?evc=") + "?evc=".length();
        int endIndex = link.indexOf("&locale=");
        return link.substring(beginIndex, endIndex);
    }

    public Error CreateUserWithError(CreateUserRequest createUserRequest, boolean needVerifyEmail, int expectedResponseCode, String errorCode) throws Exception {
        Error error = loginClient.CreateUserWithError(createUserRequest, expectedResponseCode, errorCode);
        return error;
    }

    public String getUserAccessToken(String username, String password) throws Exception {
        return oAuthClient.postUserAccessToken(username, password);
    }

    public AuthTokenResponse CreateUser(CreateUserRequest createUserRequest, boolean needVerifyEmail) throws Exception {
        return CreateUser(createUserRequest, needVerifyEmail, 200);
    }

    public UserNameCheckResponse CheckUserName(String userName, String email) throws Exception {
        UserNameCheckRequest request = new UserNameCheckRequest();
        request.setUsername(userName);
        request.setEmail(email);
        return loginClient.CheckUserName(request);
    }

    public com.junbo.common.error.Error CheckUserNameWithError(String userName, String email,
                                                               int expectedResponseCode, String errorCode) throws Exception {
        UserNameCheckRequest request = new UserNameCheckRequest();
        request.setUsername(userName);
        request.setEmail(email);
        return loginClient.CheckUserNameWithError(request, expectedResponseCode, errorCode);
    }

    public EmailCheckResponse CheckEmail(String email) throws Exception {
        EmailCheckRequest request = new EmailCheckRequest();
        request.setEmail(email);
        return loginClient.CheckEmail(request);
    }

    public Error CheckEmailWithError(String email, int expectedResponseCode, String errorCode) throws Exception {
        EmailCheckRequest request = new EmailCheckRequest();
        request.setEmail(email);
        return loginClient.CheckEmailWithError(request, expectedResponseCode, errorCode);
    }

    public Error SignInWithError(String username, String type, String password, int expectedCode, String errorCode) throws Exception {
        UserSignInRequest userSignInRequest = new UserSignInRequest();
        userSignInRequest.setEmail(username);
        UserCredential userCredential = new UserCredential();
        userCredential.setType(type);
        userCredential.setValue(password);
        userSignInRequest.setUserCredential(userCredential);
        return loginClient.signInWithError(userSignInRequest, expectedCode, errorCode);
    }

    public AuthTokenResponse SignIn(String username, String password, int expectedCode) throws Exception {
        UserSignInRequest userSignInRequest = new UserSignInRequest();
        userSignInRequest.setEmail(username);
        UserCredential userCredential = new UserCredential();
        userCredential.setType("PASSWORD");
        userCredential.setValue(password);
        userSignInRequest.setUserCredential(userCredential);
        return loginClient.signIn(userSignInRequest, expectedCode);
    }

    public AuthTokenResponse SignIn(String userName, String password) throws Exception {
        return SignIn(userName, password, 200);
    }

    public Error RateUserCredentialWithError(String password, String username, int expectedErrorCode, String errorCode) throws Exception {
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

        return loginClient.rateUserCredentialWithError(userCredentialRateRequest, expectedErrorCode, errorCode);
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

    public InstrumentUpdateResponse CreateCreditCard(String uid, String accountName, String type, int expectedResponseCode) throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = new Instrument();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String encryptedString = paymentProvider.encryptCreditCardInfo(creditCardInfo);
        instrument.setAccountName(accountName);
        instrument.setAccountNum(encryptedString);
        instrument.setBillingAddress(getBillingAddress());
        instrument.setType(type);
        instrument.setIsDefault(false);
        //instrument.setStoredValueCurrency("USD");
        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest, expectedResponseCode);
    }

    public InstrumentUpdateResponse CreateCreditCardWithoutBillingAddress(String uid) throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = new Instrument();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String encryptedString = paymentProvider.encryptCreditCardInfo(creditCardInfo);
        instrument.setAccountName(creditCardInfo.getAccountName());
        instrument.setAccountNum(encryptedString);
        instrument.setType("CREDITCARD");
        instrument.setIsDefault(false);
        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest, 400);
    }

    public InstrumentUpdateResponse CreateCreditCardWithInvalidAddress(String uid) throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = new Instrument();
        CreditCardInfo creditCardInfo = CreditCardInfo.getRandomCreditCardInfo(Country.DEFAULT);
        String encryptedString = paymentProvider.encryptCreditCardInfo(creditCardInfo);
        instrument.setAccountName(creditCardInfo.getAccountName());
        instrument.setAccountNum(encryptedString);
        instrument.setType("CREDITCARD");
        instrument.setIsDefault(false);
        instrument.setBillingAddress(getInvalidBillingAddress());
        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest, 400);
    }


    public InstrumentUpdateResponse UpdateCreditCard(InstrumentUpdateResponse response,
                                                     boolean isDefault) throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = response.getBillingProfile().getInstruments().get(1);
        instrument.setIsDefault(isDefault);
        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest);
    }

    public InstrumentUpdateResponse CreateStoredValue() throws Exception {
        return CreateStoredValue("USD", null, 200);
    }

    public InstrumentUpdateResponse CreateStoredValue(String currency, BigDecimal balance, int expectedResponseCde) throws Exception {
        InstrumentUpdateRequest instrumentUpdateRequest = new InstrumentUpdateRequest();
        Instrument instrument = new Instrument();
        //instrument.setBillingAddress(getBillingAddress());
        instrument.setType("STOREDVALUE");
        instrument.setStoredValueCurrency(currency);
        instrument.setBillingAddress(getBillingAddress());
        instrument.setStoredValueBalance(balance);

        instrumentUpdateRequest.setInstrument(instrument);
        return storeClient.updateInstrument(instrumentUpdateRequest, expectedResponseCde);
    }


    public void CreditStoredValue(String uid, BigDecimal amount) throws Exception {
        paymentProvider.creditWallet(uid, amount);
    }

    public PreparePurchaseResponse preparePurchase(String token, String offerId, PaymentInstrumentId pid,
                                                   String pin, TosId tosAcceptanceId, boolean isIAP, int expectedCode)
            throws Exception {
        PreparePurchaseRequest request = new PreparePurchaseRequest();
        request.setPurchaseToken(token);
        request.setInstrument(pid);
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
        if (isIAP) {
            Offer offer = Master.getInstance().getOffer(offerId);
            OfferRevision offerRevision = Master.getInstance().getOfferRevision(offer.getCurrentRevisionId());
            Item item = Master.getInstance().getItem(offerRevision.getItems().get(0).getItemId());
            ItemRevision itemRevision = Master.getInstance().getItemRevision(item.getCurrentRevisionId());
            Item hostItem = itemClient.getItem(itemRevision.getIapHostItemIds().get(0));
            ItemRevision hostItemRevision = itemRevisionClient.getItemRevision(hostItem.getCurrentRevisionId());
            IAPParams params = new IAPParams();
            params.setPackageName(hostItemRevision.getPackageName());
            // Todo:    This value is workaround
            params.setPackageSignatureHash(UUID.randomUUID().toString());
            params.setPackageVersion(UUID.randomUUID().toString());
            request.setIapParams(params);
        }
        return storeClient.preparePurchase(request, expectedCode);
    }

    public PreparePurchaseResponse preparePurchase(String token, String offerId, PaymentInstrumentId piid,
                                                   String pin, TosId tosAcceptanceId) throws Exception {
        return preparePurchase(token, offerId, piid, pin, tosAcceptanceId, false, 200);
    }

    public com.junbo.common.error.Error preparePurchaseWithException(String token, String offerId, PaymentInstrumentId piid,
                                                                     String pin, TosId tosAcceptanceId, boolean isIAP, int expectedResponseCode, String errorCode) throws Exception {
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
        if (isIAP) {
            Offer offer = Master.getInstance().getOffer(offerId);
            OfferRevision offerRevision = Master.getInstance().getOfferRevision(offer.getCurrentRevisionId());
            Item item = Master.getInstance().getItem(offerRevision.getItems().get(0).getItemId());
            ItemRevision itemRevision = Master.getInstance().getItemRevision(item.getCurrentRevisionId());
            Item hostItem = itemClient.getItem(itemRevision.getIapHostItemIds().get(0));
            ItemRevision hostItemRevision = itemRevisionClient.getItemRevision(hostItem.getCurrentRevisionId());
            IAPParams params = new IAPParams();
            params.setPackageName(hostItemRevision.getPackageName());
            // Todo:    This value is workaround
            params.setPackageSignatureHash(UUID.randomUUID().toString());
            params.setPackageVersion(UUID.randomUUID().toString());
            request.setIapParams(params);
        }
        return storeClient.preparePurchaseWithException(request, expectedResponseCode, errorCode);
    }

    public String getOfferIdByName(String offerName) throws Exception {
        return offerClient.getOfferIdByName(offerName);
    }

    public Item getItemByName(String itemName) throws Exception {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("q", Collections.singletonList("name:" + itemName));
        Results<Item> itemResults = itemClient.getItems(params);
        return itemResults.getItems().isEmpty() ? null : itemResults.getItems().iterator().next();
    }

    public Offer getOfferByItem(String itemId) throws Exception {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("itemId", Collections.singletonList(itemId));
        params.put("published", Collections.singletonList("true"));
        List<Offer> offers = offerClient.getOffers(params).getItems();
        return offers.isEmpty() ? null : offers.iterator().next();
    }

    public Offer getOfferByOfferId(String offerId) throws Exception {
        return getOfferByOfferId(offerId, false);
    }

    public Offer getOfferByOfferId(String offerId, boolean ignoreError) throws Exception {
        Offer offer = Master.getInstance().getOffer(offerId);
        if (offer == null) {
            try {
                offer = offerClient.getOffer(offerId, ignoreError ? 0 : 200);
            } catch (Exception ex) {
                if (!ignoreError) {
                    throw ex;
                }
            }
        }
        return offer;
    }

    public OfferRevision getOfferRevision(String offerRevisionId) throws Exception{
        OfferRevision offerRevision = Master.getInstance().getOfferRevision(offerRevisionId);
        if (offerRevision == null) {
            offerRevision = offerRevisionClient.getOfferRevision(offerRevisionId);
        }
        return offerRevision;
    }

    public Item getItemByItemId(String itemId) throws Exception{
        return getItemByItemId(itemId, false);
    }

    public Item getItemByItemId(String itemId, boolean ignoreError) throws Exception{
        Item item = Master.getInstance().getItem(itemId);
        if (item == null) {
            try {
                item = itemClient.getItem(itemId, ignoreError ? 0 : 200);
            } catch (Exception ex) {
                if (!ignoreError) {
                    throw ex;
                }
            }
        }
        return item;
    }

    public ItemRevision getItemRevision(String itemRevisionId) throws Exception {
        ItemRevision itemRevision = Master.getInstance().getItemRevision(itemRevisionId);
        if (itemRevision == null) {
            itemRevision = itemRevisionClient.getItemRevision(itemRevisionId);
        }
        return itemRevision;
    }

    public ItemAttribute getItemAttribute(String itemAttributeId) throws Exception {
        ItemAttribute itemAttribute = Master.getInstance().getItemAttribute(itemAttributeId);
        if (itemAttribute == null) {
            itemAttribute = itemAttributeClient.getItemAttribute(itemAttributeId, 0, false);
        }
        return itemAttribute;
    }

    public OfferAttribute getOfferAttribute(String offerAttributeId) throws Exception {
        OfferAttribute offerAttribute = Master.getInstance().getOfferAttribute(offerAttributeId);
        if (offerAttribute == null) {
            offerAttribute = offerAttributeClient.getOfferAttribute(offerAttributeId, 0, false);
        }
        return offerAttribute;
    }

    public CommitPurchaseResponse commitPurchase(String uid, String purchaseToken) throws Exception {
        return commitPurchase(uid, purchaseToken, 200);
    }

    public CommitPurchaseResponse commitPurchase(String uid, String purchaseToken, int expectedResponseCode) throws Exception {
        CommitPurchaseRequest commitPurchaseRequest = new CommitPurchaseRequest();
        commitPurchaseRequest.setPurchaseToken(purchaseToken);
        //commitPurchaseRequest.setChallengeSolution();
        return storeClient.commitPurchase(commitPurchaseRequest, expectedResponseCode);
    }

    public IAPEntitlementConsumeResponse iapConsumeEntitlement(EntitlementId entitlementId, String offerId)
            throws Exception {
        IAPEntitlementConsumeRequest request = new IAPEntitlementConsumeRequest();
        request.setTrackingGuid(UUID.randomUUID().toString());
        request.setEntitlement(entitlementId);
        request.setUseCountConsumed(1);
        Offer offer = offerClient.getOffer(offerId);
        OfferRevision offerRevision = offerRevisionClient.getOfferRevision(offer.getCurrentRevisionId());
        Item item = itemClient.getItem(offerRevision.getItems().get(0).getItemId());
        ItemRevision itemRevision = itemRevisionClient.getItemRevision(item.getCurrentRevisionId());
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

    private Address getInvalidBillingAddress() throws Exception {
        Address address = new Address();
        address.setStreet1("800 West Campbell Road");
        address.setCity("Richardson");
        address.setPostalCode("####");
        address.setSubCountry("TX");
        address.setCountry("US");
        return address;
    }

    public MakeFreePurchaseResponse makeFreePurchase(String offerId, TosId tosId) throws Exception {
        return makeFreePurchase(offerId, tosId, 200);
    }

    public MakeFreePurchaseResponse makeFreePurchase(String offerId, TosId tosId, int expectedResponseCode) throws Exception {
        MakeFreePurchaseRequest request = new MakeFreePurchaseRequest();
        request.setOffer(new OfferId(offerId));
        if (tosId != null) {
            ChallengeAnswer challengeAnswer = new ChallengeAnswer();
            challengeAnswer.setType("TOS_ACCEPTANCE");
            challengeAnswer.setAcceptedTos(tosId);
            request.setChallengeAnswer(challengeAnswer);
        }
        return storeClient.makeFreePurchase(request, expectedResponseCode);
    }

    public DetailsResponse getItemDetails(String itemId, int expectedResponseCode) throws Exception {
        DetailsRequest request = new DetailsRequest();
        request.setItemId(new ItemId(itemId));
        return storeClient.getDetails(request, expectedResponseCode);
    }

    public DetailsResponse getItemDetails(String itemId) throws Exception {
        return getItemDetails(itemId, 200);
    }

    public AuthTokenResponse signIn(String userName) throws Exception {
        UserSignInRequest request = new UserSignInRequest();
        request.setEmail(userName);
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

    public com.junbo.common.error.Error updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest, int expectedResponseCode, String errorCode) throws Exception {
        return storeClient.updateUserProfileReturnError(userProfileUpdateRequest, expectedResponseCode, errorCode);
    }

    public UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest,
                                                       int expectedResponseCode) throws Exception {
        return storeClient.updateUserProfile(userProfileUpdateRequest, expectedResponseCode);
    }

    public Error updateUserProfileWithError(UserProfileUpdateRequest userProfileUpdateRequest, int expectedResponseCode, String errorCode) throws Exception {
        return storeClient.updateUserProfileReturnError(userProfileUpdateRequest, expectedResponseCode, errorCode);
    }

    public UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest)
            throws Exception {
        return updateUserProfile(userProfileUpdateRequest, 200);
    }

    public VerifyEmailResponse verifyEmail(VerifyEmailRequest verifyEmailRequest) throws Exception {
        return verifyEmail(verifyEmailRequest, 200);
    }

    public VerifyEmailResponse verifyEmail(VerifyEmailRequest verifyEmailRequest, int exceptedResponseCode)
            throws Exception {
        return storeClient.verifyEmail(verifyEmailRequest, exceptedResponseCode);
    }

    public EntitlementsGetResponse getEntitlement() throws Exception {
        return storeClient.getEntitlement();
    }

    public Error getTokenWithError(String refreshToken, int expectedCode, String errorCode) throws Exception {
        AuthTokenRequest request = new AuthTokenRequest();
        request.setRefreshToken(refreshToken);
        return loginClient.getTokenWithError(request, expectedCode, errorCode);
    }

    public AuthTokenResponse getToken(String refreshToken, int expectedCode) throws Exception {
        AuthTokenRequest request = new AuthTokenRequest();
        request.setRefreshToken(refreshToken);
        return loginClient.getToken(request, expectedCode);
    }

    public AuthTokenResponse getToken(String refreshToken) throws Exception {
        return getToken(refreshToken, 200);
    }

    public BillingProfileGetResponse getBillingProfile(String offerId) throws Exception {
        return getBillingProfile(offerId, 200);
    }

    public BillingProfileGetResponse getBillingProfile(String offerId, int expectedCode) throws Exception {
        BillingProfileGetRequest request = new BillingProfileGetRequest();
        request.setOffer(offerId == null ? null : new OfferId(offerId));
        return storeClient.getBillingProfile(request, expectedCode);
    }


    public LibraryResponse getLibrary() throws Exception {
        return storeClient.getLibrary();
    }

    public TocResponse getToc() throws Exception {
        return storeClient.getTOC();
    }

    public TocResponse getToc(int expectedCode) throws Exception {
        return storeClient.getTOC(expectedCode);
    }

    public ListResponse getList(ListRequest request) throws Exception {
        return storeClient.getList(request);
    }

    public ListResponse getList(String category, String criteria, String cursor, Integer count) throws Exception {
        ListRequest listRequest = new ListRequest();
        listRequest.setCategory(category);
        listRequest.setCriteria(criteria);
        listRequest.setCursor(cursor);
        listRequest.setCount(count);
        return getList(listRequest);
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
        return getDelivery(itemId, null, 200);
    }

    public DeliveryResponse getDelivery(ItemId itemId, Integer desiredVersionCode, int expectedResponseCode) throws Exception {
        DeliveryRequest request = new DeliveryRequest();
        request.setItemId(itemId);
        request.setDesiredVersionCode(desiredVersionCode);
        return storeClient.getDelivery(request, expectedResponseCode);
    }

    public ReviewsResponse getReviews(ItemId itemId, String cursor, Integer count) throws Exception {
        ReviewsRequest reviewsRequest = new ReviewsRequest();
        reviewsRequest.setItemId(itemId);
        reviewsRequest.setCursor(cursor);
        reviewsRequest.setCount(count);
        return storeClient.getReviews(reviewsRequest);
    }

    public CaseyEmulatorData postCaseyEmulatorData(CaseyEmulatorData data) throws Exception {
        return caseyEmulatorClient.postEmulatorData(data);
    }

    public CaseyEmulatorData postCaseyEmulatorData(List<CaseyReview> caseyReviewList, List<CaseyAggregateRating> ratingList, CmsPage cmsPage) throws Exception {
        CaseyEmulatorData data = new CaseyEmulatorData();
        data.setCaseyAggregateRatings(ratingList);
        data.setCaseyReviews(caseyReviewList);
        if (cmsPage != null) {
            data.setCmsPages(Arrays.asList(cmsPage));
        }
        data.setCmsPageOffers(new HashMap<String, List<OfferId>>());
        return caseyEmulatorClient.postEmulatorData(data);
    }

    public CaseyEmulatorData postCaseyEmulatorData(CmsPage cmsPage, Map<String, List<OfferId>> offerIds) throws Exception {
        CaseyEmulatorData data = new CaseyEmulatorData();
        data.setCaseyAggregateRatings(new ArrayList<CaseyAggregateRating>());
        data.setCaseyReviews(new ArrayList<CaseyReview>());
        if (cmsPage != null) {
            data.setCmsPages(Arrays.asList(cmsPage));
        }
        data.setCmsPageOffers(offerIds);
        return caseyEmulatorClient.postEmulatorData(data);
    }

    public CaseyEmulatorData postCaseyEmulatorData(List<CmsSchedule> cmsSchedule, List<CmsPage> pages, Map<String, List<OfferId>> offerIds) throws Exception {
        CaseyEmulatorData data = new CaseyEmulatorData();
        data.setCaseyAggregateRatings(new ArrayList<CaseyAggregateRating>());
        data.setCaseyReviews(new ArrayList<CaseyReview>());
        data.setCmsSchedules(cmsSchedule);
        data.setCmsPages(pages);
        data.setCmsPageOffers(offerIds);
        return caseyEmulatorClient.postEmulatorData(data);
    }

    public void clearCache() throws Exception {
        storeConfigService.clearCache();
    }

    public DeliveryResponse getDeliveryByOfferId(String offerId) throws Exception {
        OfferRevision offerRevision = Master.getInstance().getOfferRevision(Master.getInstance().getOffer(offerId).getCurrentRevisionId());
        String itemId = offerRevision.getItems().get(0).getItemId();
        DeliveryRequest deliveryRequest = new DeliveryRequest();
        deliveryRequest.setItemId(new ItemId(itemId));
        return storeClient.getDelivery(deliveryRequest);
    }

    public String postPayment(String uid, PaymentInstrumentBase payment) throws Exception {
        return paymentProvider.postPaymentInstrument(uid, payment);
    }

    public String createUser(UserInfo userInfo) throws Exception {
        return identityClient.PostUser(userInfo);
    }

    public Organization getOrganization(OrganizationId organizationId, int statusCode) throws Exception {
        String orgIdString = IdFormatter.encodeId(organizationId);
        Organization organization = Master.getInstance().getOrganization(orgIdString);
        if (organization == null) {
            organization = organizationClient.getOrganization(organizationId, statusCode);
        }
        return organization;
    }

    public Order getOrder(OrderId orderId) throws Exception {
        orderClient.getOrderByOrderId(IdFormatter.encodeId(orderId));
        return Master.getInstance().getOrder(IdFormatter.encodeId(orderId));
    }

    public AddReviewResponse addReview(AddReviewRequest request, int expectedCode) throws Exception {
        return storeClient.addReview(request, expectedCode);
    }

}
