/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.store.utility;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.order.spec.model.PaymentInfo;
import com.junbo.store.spec.model.Entitlement;
import com.junbo.store.spec.model.billing.BillingProfile;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.Instrument;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.identity.StoreUserProfile;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Entities.paymentInstruments.CreditCardInfo;
import com.junbo.test.common.Entities.paymentInstruments.PaymentInstrumentBase;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreValidationHelper extends BaseValidationHelper {
    OfferService offerClient = OfferServiceImpl.instance();

    public void verifyAddNewCreditCard(InstrumentUpdateResponse response) {
        BillingProfile billingProfile = response.getBillingProfile();
        if (billingProfile.getInstruments().size() <= 0) {
            throw new TestException("missing payment instrument");
        }
        Instrument instrument = billingProfile.getInstruments().get(0); //verify the first item
        verifyEqual(instrument.getType(), "CREDITCARD", "verify paymentType");
        verifyEqual(instrument.getAccountNum(), "1111", "verify account number");
        verifyEqual(instrument.getExpireDate(), "2016-6", "verify expire date");
    }

    public void verifyPreparePurchase(PreparePurchaseResponse response) {
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

        verifyEqual(entitlement.getItemType(), item.getType(), "verify item type");
        verifyEqual(IdConverter.idToHexString(entitlement.getItem()), item.getId(), "verify item id");

    }

    public void verifySignInResponse(AuthTokenResponse createResponse, AuthTokenResponse signInResponse) {
        verifyEqual(signInResponse.getUsername(), createResponse.getUsername(), "verify user name");
        verifyEqual(signInResponse.getExpiresIn(), createResponse.getExpiresIn(), "verify expires in");
        verifyEqual(signInResponse.getUserId().getValue(), createResponse.getUserId().getValue(), "verify user id");
    }

    public void verifyUserProfile(UserProfileGetResponse userProfileGetResponse, AuthTokenResponse createResponse) {
        StoreUserProfile userProfile = userProfileGetResponse.getUserProfile();
        verifyEqual(userProfile.getUserId().getValue(), createResponse.getUserId().getValue(), "verify user id");
        verifyEqual(userProfile.getUsername(), createResponse.getUsername(), "verify user name");
        verifyEqual(userProfile.getPassword(), "******", "verify password");
        verifyEqual(userProfile.getPin(), "****", "verify pin");
    }

    public void verifyEWallet(InstrumentUpdateResponse response){
        BillingProfile billingProfile = response.getBillingProfile();
        if (billingProfile.getInstruments().size() <= 0) {
            throw new TestException("missing payment instrument");
        }
        Instrument instrument = billingProfile.getInstruments().get(0); //verify the first item
        verifyEqual(instrument.getType(), "STOREDVALUE", "verify payment type");
        verifyEqual(instrument.getStoredValueCurrency(), "USD", "verify stored value currency");
    }

}
