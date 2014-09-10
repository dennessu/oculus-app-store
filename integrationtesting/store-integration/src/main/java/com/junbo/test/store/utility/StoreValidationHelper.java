/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.test.store.utility;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.store.spec.model.Challenge;
import com.junbo.store.spec.model.Entitlement;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.BillingProfile;
import com.junbo.store.spec.model.billing.Instrument;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.browse.SectionLayoutResponse;
import com.junbo.store.spec.model.browse.TocResponse;
import com.junbo.store.spec.model.browse.document.AggregatedRatings;
import com.junbo.store.spec.model.browse.document.Review;
import com.junbo.store.spec.model.browse.document.SectionInfo;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.store.spec.model.identity.StoreUserProfile;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.impl.OfferRevisionServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.Utility.BaseValidationHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.util.ObjectUtils;
import org.testng.Assert;

import java.util.List;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreValidationHelper extends BaseValidationHelper {
    OfferService offerClient = OfferServiceImpl.instance();
    OfferRevisionService offerRevisionClient = OfferRevisionServiceImpl.instance();
    ItemService itemClient = ItemServiceImpl.instance();
    ItemRevisionService itemRevisionClient = ItemRevisionServiceImpl.instance();

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

    public void verifyEntitlementResponse(EntitlementsGetResponse entitlementsGetResponse, String offerId){
        OfferRevision offerRevision = Master.getInstance().getOfferRevision(Master.getInstance().getOffer(offerId).getCurrentRevisionId());
        Item item =  Master.getInstance().getItem(offerRevision.getItems().get(0).getItemId());
        Entitlement entitlement = entitlementsGetResponse.getEntitlements().get(0);

        verifyEqual(entitlement.getItemType(), item.getType(), "verify item type");
        verifyEqual(entitlement.getEntitlementType(), "DOWNLOAD","verify entitlement type");
        verifyEqual(entitlement.getItem().getValue(), item.getId(), "verify item id");

    }

    public void verifyCommitPurchase(CommitPurchaseResponse response, String offerId) throws Exception {
        Offer offer = offerClient.getOffer(offerId);
        OfferRevision offerRevision = offerRevisionClient.getOfferRevision(offer.getCurrentRevisionId());
        Item item = itemClient.getItem(offerRevision.getItems().get(0).getItemId());
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


    public void verifySectionLayoutBreadcrumbs(SectionLayoutResponse layout, SectionLayoutResponse parentLayout, SectionInfo parentSection) throws Exception {
        if (parentLayout != null) {
            Assert.assertEquals(layout.getBreadcrumbs().size(), parentLayout.getBreadcrumbs().size() + 1, "Breadcrumbs length not corret");
            for (int i = 0;i < parentLayout.getBreadcrumbs().size(); ++i) {
                Validator.Validate("node in breadcrumbs not equal", layout.getBreadcrumbs().get(i), parentLayout.getBreadcrumbs().get(i));
            }
        }

        if (parentSection != null) {
            SectionInfo last = layout.getBreadcrumbs().get(layout.getBreadcrumbs().size() - 1);
            Assert.assertEquals(last.getCriteria(), parentSection.getCriteria());
            Assert.assertEquals(last.getCategory(), parentSection.getCategory());
            Assert.assertEquals(last.getName(), parentSection.getName());
        }
    }

    public void verifyItemsInList(List<String> names, final List<com.junbo.store.spec.model.browse.document.Item> items, boolean verifySameSize) {
        for (final String name : names) {
            Object result = CollectionUtils.find(items, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    com.junbo.store.spec.model.browse.document.Item item = (com.junbo.store.spec.model.browse.document.Item) object;
                    return name.equals(item.getTitle());
                }
            });
            Assert.assertNotNull(result, String.format("Item %s not in the list", name));
        }

        if (verifySameSize) {
            Assert.assertEquals(names.size(), items.size(), "Size of list not equals");
        }
    }

    public void verifyReview(Review review, CaseyReview caseyReview, StoreUserProfile storeUserProfile) {
        Assert.assertEquals(review.getAuthorName(), storeUserProfile.getNickName());
        Assert.assertEquals(review.getContent(), caseyReview.getReview());
        Assert.assertEquals(review.getTitle(), caseyReview.getReviewTitle());
        Assert.assertEquals(review.getStarRatings().size(), caseyReview.getRatings().size());
        for (CaseyReview.Rating rating : caseyReview.getRatings()) {
            Assert.assertEquals(review.getStarRatings().get(rating.getType()), rating.getScore(), "rating result not correct");
        }
    }

    public void verifyAggregateRatings(List<AggregatedRatings> aggregatedRatings, List<CaseyAggregateRating> caseyAggregatedRatings) {
        Assert.assertEquals(aggregatedRatings.size(), caseyAggregatedRatings.size());
        for (final AggregatedRatings rating : aggregatedRatings) {
            CaseyAggregateRating caseyAggregateRating = (CaseyAggregateRating) CollectionUtils.find(caseyAggregatedRatings, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return ObjectUtils.nullSafeEquals(((CaseyAggregateRating) object).getType(), rating.getType());
                }
            });

            Assert.assertEquals(rating.getAverageRating(), caseyAggregateRating.getAverage(), "average rating not correct");
            Assert.assertEquals(rating.getRatingsCount(), caseyAggregateRating.getCount(), "rating count not correct");
            Assert.assertEquals(rating.getType(), caseyAggregateRating.getType(), "type not correct");
            Assert.assertNull(rating.getCommentsCount(), "comments count should be null");

            for (int i = 0;i < caseyAggregateRating.getHistogram().length; ++i) {
                Assert.assertEquals(rating.getRatingsHistogram().get(i), caseyAggregateRating.getHistogram()[i], "rating histogram not correct");
            }
        }
    }
}

