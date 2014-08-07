/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;


import com.junbo.common.id.EntitlementId;
import com.junbo.store.spec.model.billing.BillingProfileGetRequest;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.store.spec.model.purchase.SelectInstrumentResponse;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreTesting extends BaseTestClass {

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test iap offer checkout",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Post prepare purchase",
                    "4. Verify price response",
                    "5. Select payment instrument for purchase",
                    "6. Commit purchase",
                    "7. Verify purchase response",
                    "8. Consume iap entitlement",
                    "9. Get entitlement",
                    "10. Verify entitlement response"
            }
    )
    @Test
    public void testIAPCheckoutByCreditCard() throws Exception {
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser();

        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        Master.getInstance().setCurrentUid(uid); //required

        BillingProfileUpdateResponse billingProfileUpdateResponse = testDataProvider.CreateCreditCard(uid);

        Long paymentId = billingProfileUpdateResponse.getBillingProfile().getInstruments().get(0).getInstrumentId().getValue();

        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(uid, offer_iap_normal);
        //TODO validation

        String purchaseToken = preparePurchaseResponse.getPurchaseToken();

        SelectInstrumentResponse selectInstrumentResponse = testDataProvider.selectInstrument(uid, purchaseToken, paymentId);

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        //TODO validation

        EntitlementId entitlementId = commitPurchaseResponse.getEntitlements().get(0).getEntitlementId();

        IAPEntitlementConsumeResponse iapEntitlementConsumeResponse = testDataProvider.iapConsumeEntitlement(uid, entitlementId);

        //TODO validation

    }
}
