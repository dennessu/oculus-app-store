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
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OrderId;
import com.junbo.store.spec.model.Challenge;
import com.junbo.store.spec.model.Entitlement;
import com.junbo.store.spec.model.billing.BillingProfile;
import com.junbo.store.spec.model.billing.Instrument;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.browse.LibraryResponse;
import com.junbo.store.spec.model.browse.TocResponse;
import com.junbo.store.spec.model.identity.StoreUserProfile;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import org.testng.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreValidationHelper extends ValidationHelper {

    private static final TreeMap<Integer, String> lengthToImageSizeGroup = new TreeMap<>();

    private StoreTestDataProvider storeTestDataProvider;

    interface VerifyEqual<T> {
        void verify(T o1, T o2);
    }

    static {
        lengthToImageSizeGroup.put(2560, "large");
        lengthToImageSizeGroup.put(1440, "medium");
        lengthToImageSizeGroup.put(690, "small");
        lengthToImageSizeGroup.put(336, "tiny");
    }

    public StoreValidationHelper(StoreTestDataProvider storeTestDataProvider) {
        this.storeTestDataProvider = storeTestDataProvider;
    }

    public void verifyAddNewCreditCard(InstrumentUpdateResponse response) {
        BillingProfile billingProfile = response.getBillingProfile();
        if (billingProfile.getInstruments().size() <= 0) {
            throw new TestException("missing payment instrument");
        }
        Instrument instrument = billingProfile.getInstruments().get(0); //verify the first item
        verifyEqual(instrument.getType(), "CREDITCARD", "verify paymentType");
        verifyEqual(instrument.getAccountNum(), "2927", "verify account number");
        verifyEqual(instrument.getExpireDate(), "2016-06", "verify expire date");
    }

    public void verifyPreparePurchase(PreparePurchaseResponse response, boolean hasTax, OrderId orderId) {
        verifyEqual(response.getFormattedTotalPrice(),hasTax ? String.format("$10.83") : String.format("$10.83"), "verify formatted total price");
        if (response.getPurchaseToken() == null || response.getPurchaseToken().isEmpty()) {
            throw new TestException("missing purchase token in prepare purchase response");
        }
        if (orderId != null) {
            Assert.assertEquals(response.getOrder(), orderId);
        }
    }

    public void verifyLibraryResponse(LibraryResponse response, ItemId itemId) throws Exception {
        Item item = storeTestDataProvider.getItemByItemId(itemId.getValue());
        ItemRevision itemRevision = storeTestDataProvider.getItemRevision(item.getCurrentRevisionId());
        com.junbo.store.spec.model.browse.document.Item responseItem = null;
        for (com.junbo.store.spec.model.browse.document.Item e : response.getItems()) {
            if (e.getSelf().equals(itemId)) {
                responseItem = e;
                break;
            }
        }

        Assert.assertNotNull(responseItem);
        verifyEqual(responseItem.getItemType(), item.getType(), "verify item type");
        verifyEqual(responseItem.getTitle(), itemRevision.getLocales().get("en_US").getName(), "verify entitlement type");
        verifyEqual(responseItem.getOwnedByCurrentUser(), Boolean.valueOf(true), "verify owned by current user");
    }

    public void verifyItemsInLibrary(LibraryResponse response, List<String> itemNames) throws Exception {
        Set<ItemId> itemIdSet = new HashSet<>();
        for (String itemName : itemNames) {
            itemIdSet.add(new ItemId(storeTestDataProvider.getItemByName(itemName).getId()));
        }
        Set<ItemId> actual = new HashSet<>();
        for (com.junbo.store.spec.model.browse.document.Item item : response.getItems()) {
            Assert.assertTrue(!actual.contains(item.getSelf()), "duplicate item found in library");
            actual.add(item.getSelf());
        }
        Assert.assertEquals(actual, itemIdSet);
    }

    public void verifyCommitPurchase(CommitPurchaseResponse response, String offerId) throws Exception {
        Offer offer = storeTestDataProvider.getOfferByOfferId(offerId);
        OfferRevision offerRevision = storeTestDataProvider.getOfferRevision(offer.getCurrentRevisionId());
        com.junbo.catalog.spec.model.item.Item item = storeTestDataProvider.getItemByItemId(offerRevision.getItems().get(0).getItemId());
        Entitlement entitlement = response.getEntitlements().get(0);

        verifyEqual(entitlement.getItemType(), item.getType(), "verify item type");
        verifyEqual(IdConverter.idToHexString(entitlement.getItem()), item.getId(), "verify item id");
    }

    public void verifySignInResponse(AuthTokenResponse createResponse, AuthTokenResponse signInResponse) {
        verifyEqual(signInResponse.getUsername(), createResponse.getUsername(), "verify user name");
        assert signInResponse.getExpiresIn() > 3500;
        verifyEqual(signInResponse.getUserId().getValue(), createResponse.getUserId().getValue(), "verify user id");
    }

    public void verifyEmailInAuthResponse(AuthTokenResponse authTokenResponse, String email, boolean isValidated) {
        Assert.assertEquals(authTokenResponse.getEmail().getValue(), email);
        Assert.assertEquals(authTokenResponse.getEmail().getIsValidated().booleanValue(), isValidated);
    }

    public void verifyUserProfile(UserProfileGetResponse userProfileGetResponse, AuthTokenResponse createResponse) {
        StoreUserProfile userProfile = userProfileGetResponse.getUserProfile();
        verifyEqual(userProfile.getUserId().getValue(), createResponse.getUserId().getValue(), "verify user id");
        verifyEqual(userProfile.getUsername(), createResponse.getUsername(), "verify user name");
        verifyEqual(userProfile.getPassword(), "******", "verify password");
        verifyEqual(userProfile.getPin(), "****", "verify pin");
    }

    public void verifyEWallet(InstrumentUpdateResponse response) {
        BillingProfile billingProfile = response.getBillingProfile();
        if (billingProfile.getInstruments().size() <= 0) {
            throw new TestException("missing payment instrument");
        }
        Instrument instrument = billingProfile.getInstruments().get(0); //verify the first item
        verifyEqual(instrument.getType(), "STOREDVALUE", "verify payment type");
        verifyEqual(instrument.getStoredValueCurrency(), "USD", "verify stored value currency");
    }

    public void verifyTocTosChallenge(Challenge challenge) {
        Assert.assertNotNull(challenge);
        Assert.assertEquals(challenge.getType(), "TOS_ACCEPTANCE");
        Assert.assertNotNull(challenge.getTos());
        Assert.assertFalse(challenge.getTos().getAccepted());
        Assert.assertNotNull(challenge.getTos().getContent());
        Assert.assertNotNull(challenge.getTos().getTitle());
        Assert.assertNotNull(challenge.getTos().getVersion());
        Assert.assertEquals(challenge.getTos().getType(), "TOS");
    }

    public void verifyToc(TocResponse tocResponse) {
        Assert.assertNull(tocResponse.getChallenge());
        Assert.assertEquals(tocResponse.getSections().size(), 4); // current 3 sections will be returned.
    }

    public void verifyIapLibrary(LibraryResponse libraryResponse, CommitPurchaseResponse commitPurchaseResponse) {
        Entitlement entitlement = commitPurchaseResponse.getEntitlements().get(0);
        com.junbo.store.spec.model.browse.document.Item item = libraryResponse.getItems().get(0);
        com.junbo.store.spec.model.browse.document.Item expectedItem = entitlement.getItemDetails();
        verifyEqual(item.getItemType(), expectedItem.getItemType(), "verify item type");
        verifyEqual(item.getTitle(), expectedItem.getTitle(), "verify item title");
        //verifyEqual(item.getPayload(), expectedItem.getPayload(), "verify item payload");
        //verifyEqual(item.getSignature(), expectedItem.getSignature(), "verify item signature");
        verifyEqual(item.getOwnedByCurrentUser(), Boolean.valueOf(true), "verify item owned by user");
        verifyEqual(item.getIapDetails().getSku(), expectedItem.getIapDetails().getSku(), "verify item sku");
    }
}

