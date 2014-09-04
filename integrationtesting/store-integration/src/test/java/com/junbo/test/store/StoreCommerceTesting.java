package com.junbo.test.store;

import com.junbo.catalog.spec.model.offer.Action;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.Instrument;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.MakeFreePurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by weiyu_000 on 8/29/14.
 */
public class StoreCommerceTesting extends BaseTestClass {

    @Property(
            priority = Priority.BVT,
            features = "Store commerce",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test add new credit card ",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. verify response",
            }
    )
    @Test
    public void testAddCreditCard() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());

        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCard(uid);
        //verify decrypted credit card info
        validationHelper.verifyAddNewCreditCard(instrumentUpdateResponse);
    }


    @Property(
            priority = Priority.BVT,
            features = "Store commerce",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test update credit card ",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Add another credit card into billing profile",
                    "3. Update the second credit card as default payment",
                    "3. verify response the second cc is default pi",
            }
    )
    @Test
    public void testUpdateCreditCard() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());

        InstrumentUpdateResponse instrumentUpdateResponse1 = testDataProvider.CreateCreditCard(uid);
        //verify decrypted credit card info
        InstrumentUpdateResponse instrumentUpdateResponse2 = testDataProvider.CreateCreditCard(uid);

        InstrumentUpdateResponse instrumentUpdateResponse3 = testDataProvider.UpdateCreditCard(instrumentUpdateResponse2, true);

        assert instrumentUpdateResponse3.getBillingProfile().getInstruments().get(1).getIsDefault().equals(true);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store commerce",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get billing profile",
            steps = {
                    "1. Create user",
                    "2. Get billing profile by invalid offer id",
                    "3. Verify billing profile response",
            }
    )
    @Test
    public void testGetBillingProfileByInvalidOfferId() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());

        BillingProfileGetResponse response = testDataProvider.getBillingProfile(null);
        response = testDataProvider.getBillingProfile("123", 404);
        assert response == null;
    }


    @Property(
            priority = Priority.BVT,
            features = "Store commerce",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get billing profile by different offer type",
            steps = {
                    "1. Create user",
                    "2. Get billing profile by digital offer",
                    "3. Verify billing profile response",
                    "4. Add credit card into billing profile",
                    "5. Get billing profile response by digital offer",
                    "6. Checkout stored value offer",
                    "7. Get billing profile by stored value",
                    "8. Verify billing profile response"
            }
    )
    @Test
    public void testGetBillingProfileFilterByOfferType() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());

        String offerId = testDataProvider.getOfferIdByName(offer_digital_normal1);
        BillingProfileGetResponse response = testDataProvider.getBillingProfile(null);
        assert response.getBillingProfile().getInstruments().size() == 0;

        testDataProvider.CreateCreditCard(uid);
        response = testDataProvider.getBillingProfile(offerId);
        assert response.getBillingProfile().getInstruments().size() == 1;
        assert response.getBillingProfile().getInstruments().get(0).getType().equals("CREDITCARD");

        offerId = testDataProvider.getOfferIdByName(offer_storedValue_normal);

        PaymentInstrumentId paymentId = response.getBillingProfile().getInstruments().get(0).getSelf();

        testDataProvider.preparePurchase(null, offerId, null, null, null, false, 412);
        testDataProvider.CreateStoredValue();

        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId());

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        response = testDataProvider.getBillingProfile(null);
        assert response.getBillingProfile().getInstruments().size() == 2;
        Instrument instrument = null;
        for (int index = 0; index < response.getBillingProfile().getInstruments().size(); index ++) {
            if (response.getBillingProfile().getInstruments().get(index).getType().equalsIgnoreCase("STOREDVALUE")) {
                instrument = response.getBillingProfile().getInstruments().get(index);
            }
        }
        assert instrument != null;

        Offer offer = Master.getInstance().getOffer(offerId);
        OfferRevision offerRevision = Master.getInstance().getOfferRevision(offer.getCurrentRevisionId());
        List<Action> actionList = offerRevision.getEventActions().get("PURCHASE");
        Action action = null;
        for(int index = 0; index < actionList.size(); index ++) {
            if (actionList.get(index).getType().equalsIgnoreCase("CREDIT_WALLET")) {
                action = actionList.get(index);
            }
        }
        assert action != null;

        assert instrument.getStoredValueBalance().equals(action.getStoredValueAmount());
        assert instrument.getStoredValueCurrency().equalsIgnoreCase(action.getStoredValueCurrency());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store commerce",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test free purchase with invalid offer id",
            steps = {
                    "1. Create user",
                    "2. Make free purchase with invalid offer id",
                    "8. Verify error response"
            }
    )
    @Test
    public void testFreePurchaseWithInvalidOfferId() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();

        AuthTokenResponse signInResponse = testDataProvider.signIn(userName);

        validationHelper.verifySignInResponse(authTokenResponse, signInResponse);

        UserProfileGetResponse userProfileResponse = testDataProvider.getUserProfile();

        validationHelper.verifyUserProfile(userProfileResponse, authTokenResponse);

        String offerId;
        if (offer_iap_free.toLowerCase().contains("test")) {
            offerId = testDataProvider.getOfferIdByName(offer_digital_free);
        } else {
            offerId = offer_digital_free;
        }

        MakeFreePurchaseResponse freePurchaseResponse = testDataProvider.makeFreePurchase("123", null, 404);

        assert Master.getInstance().getApiErrorMsg().contains("offer 123 is not found");
        assert Master.getInstance().getApiErrorMsg().contains("123.004");


    }


    @Property(
            priority = Priority.Dailies,
            features = "Store commerce",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test add instrument without billing address",
            steps = {
                    "1. Create user",
                    "2. Post credit card without billing address",
                    "8. Verify error response"
            }
    )
    @Test
    public void testFreePurchaseWithoutBillingAddress() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());

        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCardWithoutBillingAddress(uid);

        assert Master.getInstance().getApiErrorMsg().contains("Field is required");
        assert Master.getInstance().getApiErrorMsg().contains("130.001");
        assert Master.getInstance().getApiErrorMsg().contains("Input Error");

    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test prepare purchase with invalid offer id",
            steps = {
                    "1. Create user",
                    "2. Post prepare purchase with invalid offer id",
                    "3. Verify error response",
            }
    )
    @Test
    public void testPreparePurchaseWithInvalidOfferId() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        testDataProvider.preparePurchase(null, "123", null, null, null, false, 404);

        assert Master.getInstance().getApiErrorMsg().contains("offer 123 is not found");
        assert Master.getInstance().getApiErrorMsg().contains("123.004");

    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test change payment via prepare purchase",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Add another credit card into billing profile",
                    "3. Post prepare purchase",
                    "5. Select the payment instrument for purchase",
                    "6. Change the second payment via prepare purchase",
                    "6. Commit purchase",
                    "7. Verify purchase response",
            }
    )
    @Test
    public void testChangePaymentViaPreparePurchase() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user

        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCard(uid);
        //get payment id in billing profile
        PaymentInstrumentId paymentId = instrumentUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();

        InstrumentUpdateResponse instrumentUpdateResponse2 = testDataProvider.CreateCreditCard(uid);
        //get payment id in billing profile
        PaymentInstrumentId paymentId2 = instrumentUpdateResponse2.getBillingProfile().getInstruments().get(1).getSelf();

        String offerId = testDataProvider.getOfferIdByName(offer_digital_normal1);
        //post order without set payment instrument
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId());

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId2, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId());

        //verify formatted price
        validationHelper.verifyPreparePurchase(preparePurchaseResponse);

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        testDataProvider.commitPurchase(uid, purchaseToken);

        //TODO Get order to verify payment id

    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test checkout with invalid payment id",
            steps = {
                    "1. Create user",
                    "2. Post prepare purchase",
                    "3. Select the invalid payment instrument for purchase",
                    "4. Commit purchase",
                    "5. Verify error response",
            }
    )
    @Test
    public void testCheckoutWithInvalidPaymentId() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());

        PaymentInstrumentId paymentId = new PaymentInstrumentId(123L);

        String offerId = testDataProvider.getOfferIdByName(offer_digital_normal1);
        //post order without set payment instrument
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null);

        assert preparePurchaseResponse.getChallenge() != null;
        assert preparePurchaseResponse.getChallenge().getType().equalsIgnoreCase("PIN");

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        assert preparePurchaseResponse.getChallenge() != null;
        assert preparePurchaseResponse.getChallenge().getType().equalsIgnoreCase("TOS_ACCEPTANCE");
        assert preparePurchaseResponse.getChallenge().getTos() != null;

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId(),false ,400);

        assert Master.getInstance().getApiErrorMsg().contains("not found");

    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test commit purchase with invalid purchase token",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Post prepare purchase",
                    "4. Select payment instrument for purchase",
                    "5. Commit purchase with invalid purchase token",
                    "7. Verify error response",
            }
    )
    @Test
    public void testCommitPurchaseWithInvalidPurchaseToken() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());

        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCard(uid);

        PaymentInstrumentId paymentId = instrumentUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();

        String offerId = testDataProvider.getOfferIdByName(offer_digital_normal1);

        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null);

        assert preparePurchaseResponse.getChallenge() != null;
        assert preparePurchaseResponse.getChallenge().getType().equalsIgnoreCase("PIN");

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId());

        String purchaseToken = preparePurchaseResponse.getPurchaseToken();

        CreateUserRequest createUserRequest2 = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse2 = testDataProvider.CreateUser(createUserRequest2, true);
        String uid2 = IdConverter.idToHexString(authTokenResponse2.getUserId());

        InstrumentUpdateResponse instrumentUpdateResponse2 = testDataProvider.CreateCreditCard(uid2);

        PaymentInstrumentId paymentId2 = instrumentUpdateResponse2.getBillingProfile().getInstruments().get(0).getSelf();

        PreparePurchaseResponse preparePurchaseResponse2 = testDataProvider.preparePurchase(null, offerId, null, null, null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse2.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        testDataProvider.preparePurchase(preparePurchaseResponse2.getPurchaseToken(), offerId, paymentId2, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId());

        testDataProvider.commitPurchase(uid, purchaseToken, 400);

        assert Master.getInstance().getApiErrorMsg().contains("Field value is invalid");
        assert Master.getInstance().getApiErrorMsg().contains("130.001");
        assert Master.getInstance().getApiErrorMsg().contains("purchaseToken");

    }


    @Property(
            priority = Priority.Comprehensive,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test commit purchase with another purchase token",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Post prepare purchase",
                    "4. Select payment instrument for purchase",
                    "5. Do the previous steps again to get another purchase token",
                    "6. Commit purchase with another purchase token ",
                    "7. Verify error response",
            }
    )
    @Test
    public void testCommitPurchaseWithAnotherPurchaseToken() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user

        CreateUserRequest createUserRequest2 = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse2 = testDataProvider.CreateUser(createUserRequest2, true);
        String uid2 = IdConverter.idToHexString(authTokenResponse2.getUserId());
        //add new credit card to user
        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCard(uid2);
        //verify decrypted credit card info
        PaymentInstrumentId paymentId = instrumentUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();

        Master.getInstance().setCurrentUid(uid);

        String offerId = testDataProvider.getOfferIdByName(offer_digital_normal1);
        //post order without set payment instrument
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId(), false, 400);

        assert Master.getInstance().getApiErrorMsg().contains("Field value is invalid. do not belong to this user");
        assert Master.getInstance().getApiErrorMsg().contains("133.001");

    }

}