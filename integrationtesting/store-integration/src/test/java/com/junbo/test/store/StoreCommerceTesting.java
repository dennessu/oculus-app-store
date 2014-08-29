package com.junbo.test.store;

import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

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
            priority = Priority.BVT,
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

        BillingProfileGetResponse response = testDataProvider.getBillingProfile(null, Country.DEFAULT, "en_US");
        //TODO Error response validate
        response = testDataProvider.getBillingProfile("123", Country.DEFAULT, "en_US");
        //TODO Error response validate
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
        BillingProfileGetResponse response = testDataProvider.getBillingProfile(null, Country.DEFAULT, "en_US");
        assert response.getBillingProfile().getInstruments().size() == 0;

        testDataProvider.CreateCreditCard(uid);
        response = testDataProvider.getBillingProfile(offerId, Country.DEFAULT, "en_US");
        assert response.getBillingProfile().getInstruments().size() == 1;
        assert response.getBillingProfile().getInstruments().get(0).getType().equals("CREDITCARD");

        offerId = testDataProvider.getOfferIdByName(offer_storedValue_normal);

        PaymentInstrumentId paymentId = response.getBillingProfile().getInstruments().get(0).getSelf();

        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId());

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        validationHelper.verifyCommitPurchase(commitPurchaseResponse, offerId);

        response = testDataProvider.getBillingProfile(offerId, Country.DEFAULT, "en_US");
        assert response.getBillingProfile().getInstruments().size() == 1;
        assert response.getBillingProfile().getInstruments().get(0).getType().equals("STOREDVALUE");
    }


}
