/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.store.utility;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.store.spec.model.Entitlement;
import com.junbo.store.spec.model.billing.BillingProfile;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.billing.Instrument;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreValidationHelper extends BaseValidationHelper {
    OfferService offerClient = OfferServiceImpl.instance();

    public void verifyAddNewCreditCard(String uid, BillingProfileUpdateResponse response) {
        verifyEqual(response.getStatus(), String.format("SUCCESS"), "verify status");
        BillingProfile billingProfile = response.getBillingProfile();
        verifyEqual(IdConverter.idToHexString(billingProfile.getUserId()), uid, "verify user id");
        if (billingProfile.getInstruments().size() <= 0) {
            throw new TestException("missing payment instrument");
        }
        Instrument instrument = billingProfile.getInstruments().get(0); //verify the first item
        verifyEqual(instrument.getType(), "CREDITCARD", "verify paymentType");
        verifyEqual(instrument.getAccountNum(), "1111", "verify account number");
        verifyEqual(instrument.getExpireDate(), "2016-6", "verify expire date");
    }

    public void verifyPreparePurchase(PreparePurchaseResponse response) {
        verifyEqual(response.getStatus(), String.format("SUCCESS"), "verify prepare purchase status");
        verifyEqual(response.getFormattedTotalPrice(), String.format("10.0$"), "verify formatted total price");
        if (response.getPurchaseToken() == null || response.getPurchaseToken().isEmpty()) {
            throw new TestException("missing purchase token in prepare purchase response");
        }
    }

    public void verifyCommitPurchase(CommitPurchaseResponse response, String offerId) throws Exception {
        Offer offer = Master.getInstance().getOffer(offerId);
        OfferRevision offerRevision = Master.getInstance().getOfferRevision(offer.getCurrentRevisionId());
        Item item = Master.getInstance().getItem(offerRevision.getItems().get(0).getItemId());
        Entitlement entitlement = response.getEntitlements().get(0);

        verifyEqual(response.getStatus(), String.format("SUCCESS"), "verify commit status");
        verifyEqual(entitlement.getItemType(), item.getType(), "verify item type");
        verifyEqual(IdConverter.idToHexString(entitlement.getItemId()), item.getId(), "verify item id");
        verifyEqual(entitlement.getUseCount(), offerRevision.getEventActions().get("PURCHASE").get(0).getUseCount(), "verify user count");
        verifyEqual(entitlement.getIsConsumable(), Boolean.valueOf(true), "verify is consumable");

    }


}
