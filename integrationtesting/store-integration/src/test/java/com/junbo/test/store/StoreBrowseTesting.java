/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.common.id.ItemId;
import com.junbo.common.util.IdFormatter;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.browse.document.*;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.store.spec.model.identity.StoreUserProfile;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.purchase.MakeFreePurchaseResponse;
import com.junbo.test.store.utility.DataGenerator;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * The StoreBrowseTesting class.
 */
public class StoreBrowseTesting extends BaseTestClass {

    @Test
    public void testGetToc() throws Exception {
        // create user and sign in
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();
        testDataProvider.signIn(userName);

        // get toc
        TocResponse response = testDataProvider.getToc();
        validationHelper.verifyTocTosChallenge(response.getChallenge());

        // accept the tos
        AcceptTosResponse tosResponse = testDataProvider.acceptTos(response.getChallenge().getTos().getTosId());
        Assert.assertEquals(tosResponse.getTos(), response.getChallenge().getTos().getTosId());

        // get toc again
        response = testDataProvider.getToc();
        validationHelper.verifyToc(response);
    }

    @Test
    public void testGetLibrary() throws Exception {
        // create user and sign in
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();
        testDataProvider.signIn(userName);

        // call get library and expect empty items
        assert testDataProvider.getLibrary().getItems().isEmpty();

        // buy offers
        String offerId;
        if (offer_iap_free.toLowerCase().contains("test")) {
            offerId = testDataProvider.getOfferIdByName(offer_digital_free);
        } else {
            offerId = offer_digital_free;
        }
        MakeFreePurchaseResponse freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, null);

        freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, freePurchaseResponse.getChallenge().getTos().getTosId());
        Assert.assertNotNull(freePurchaseResponse.getEntitlements().get(0).getItemDetails(), "itemDetails should not be null");

        LibraryResponse libraryResponse = testDataProvider.getLibrary();
        assert libraryResponse.getItems().size() == 1; // todo add more verifications

    }

    @Test
    public void testGetDetailsPurchased() throws Exception {
        // create user and sign in
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();
        testDataProvider.signIn(userName);

        // call get library and expect empty items
        assert testDataProvider.getLibrary().getItems().isEmpty();

        // buy offers
        String offerId;
        if (offer_iap_free.toLowerCase().contains("test")) {
            offerId = testDataProvider.getOfferIdByName(offer_digital_free);
        } else {
            offerId = offer_digital_free;
        }
        MakeFreePurchaseResponse response = testDataProvider.makeFreePurchase(offerId, null);
        response = testDataProvider.makeFreePurchase(offerId, response.getChallenge().getTos().getTosId());
        Assert.assertNotNull(response.getEntitlements().get(0).getItemDetails(), "itemDetails in entitlement should not be null");

        LibraryResponse libraryResponse = testDataProvider.getLibrary();
        assert libraryResponse.getItems().size() == 1; // todo add more verifications
        ItemId itemId = libraryResponse.getItems().get(0).getSelf();

        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId.getValue());
        Assert.assertTrue(detailsResponse.getItem().getOwnedByCurrentUser());
        Assert.assertTrue(detailsResponse.getItem().getOffer().getIsFree());

        // get delivery
        DeliveryResponse deliveryResponse = testDataProvider.getDelivery(itemId);
        Assert.assertTrue(deliveryResponse.getDownloadUrl() != null);
    }

    @Test
    public void testGetDetails() throws Exception {
        // get the first item in the first section
        TocResponse response = gotoToc();
        SectionInfoNode sectionInfo = response.getSections().get(1);
        ItemId itemId = testDataProvider.getLayout(sectionInfo.getCategory(), sectionInfo.getCriteria(), null).getItems().get(0).getSelf();

        // get the item details
        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId.getValue());
        Assert.assertFalse(detailsResponse.getItem().getOwnedByCurrentUser());
    }

    @Test
    public void testGetList() throws Exception {
        // get the first item in the first section
        TocResponse response = gotoToc();
        SectionInfoNode sectionInfo = response.getSections().get(0);
        listAllItems(sectionInfo.getCategory(), sectionInfo.getCriteria(), 2, null);
    }

    @Test
    public void testExploreAllSections() throws Exception {
        // get the first item in the first section
        TocResponse response = gotoToc();
        for (SectionInfoNode sectionInfo : response.getSections()) {
            exploreSection(sectionInfo.getCategory(), sectionInfo.getCriteria());
        }
    }

    @Test
    public void testGetFeaturedSection() throws Exception {
        int pageSize = 2;
        TocResponse tocResponse = gotoToc();
        SectionInfo featuredSectionInfo = tocResponse.getSections().get(0).toSectionInfo();
        Assert.assertEquals(featuredSectionInfo.getCriteria(), featureRootCriteria, "section criteria not match");

        // validate top level feature section layout
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(featuredSectionInfo.getCategory(), featuredSectionInfo.getCriteria(), pageSize);
        Assert.assertTrue(sectionLayoutResponse.getBreadcrumbs().isEmpty(), "top level section's breadcrumbs should be empty");
        Assert.assertEquals(sectionLayoutResponse.getChildren().size(), 2);
        Assert.assertEquals(sectionLayoutResponse.getName(), "Featured");
        Assert.assertTrue(sectionLayoutResponse.getItems().isEmpty(), "top level feature section should have empty items");
        Assert.assertEquals(sectionLayoutResponse.getChildren().size(), 2);

        SectionInfo featuredOfferSection = sectionLayoutResponse.getChildren().get(0);
        SectionInfo featuredItemSection = sectionLayoutResponse.getChildren().get(1);
        Assert.assertEquals(featuredOfferSection.getCriteria(), "feature-offers");
        Assert.assertEquals(featuredItemSection.getCriteria(), "feature-items");

        // get feature-offers section
        SectionLayoutResponse featuredOfferLayout = testDataProvider.getLayout(null, featuredOfferSection.getCriteria(), pageSize);
        validationHelper.verifySectionLayoutBreadcrumbs(featuredOfferLayout, sectionLayoutResponse, featuredSectionInfo);
        Assert.assertTrue(featuredOfferLayout.getChildren().isEmpty(), "children should be empty");
        List<Item> items = getItemsInSection(featuredOfferSection.getCategory(), featuredOfferSection.getCriteria(), pageSize);
        validationHelper.verifyItemsInList(itemsInFeaturedOffer, items, true);

        // get feature-offers section
        SectionLayoutResponse featuredItemLayout = testDataProvider.getLayout(null, featuredItemSection.getCriteria(), pageSize);
        validationHelper.verifySectionLayoutBreadcrumbs(featuredItemLayout, sectionLayoutResponse, featuredSectionInfo);
        Assert.assertTrue(featuredItemLayout.getChildren().isEmpty(), "children should be empty");
        items = getItemsInSection(featuredItemSection.getCategory(), featuredItemSection.getCriteria(), pageSize);
        validationHelper.verifyItemsInList(itemsInFeaturedItem, items, true);
    }

    @Test
    public void testGetGameSection() throws Exception {
        testGetCategorySection("Game", "Game", Arrays.asList(item_digital_normal1, item_digital_oclus_free1, item_digital_free));
    }

    @Test
    public void testGetAppSection() throws Exception {
        testGetCategorySection("App", "App", Arrays.asList(item_digital_normal2, item_digital_oclus_free2));
    }

    @Test
    public void testGetReviewAndAggregateRating() throws Exception {
        gotoToc();
        StoreUserProfile userProfile = testDataProvider.getUserProfile().getUserProfile();

        // prepare review & aggregate ratings
        int numOfReviews = 4; // todo change the number after the pagination issue is fixed
        List<CaseyReview> caseyReviews = new ArrayList<>();
        for (int i = 0; i < numOfReviews;++i) {
            caseyReviews.add(DataGenerator.instance().generateCaseyReview(IdFormatter.encodeId(userProfile.getUserId())));
        }
        List<CaseyAggregateRating> caseyAggregateRating = Arrays.asList(DataGenerator.instance().generateCaseyAggregateRating("quality"), DataGenerator.instance().generateCaseyAggregateRating("comfort"));
        testDataProvider.postCaseyEmulatorData(caseyReviews, caseyAggregateRating);
        testDataProvider.clearCache(); // clear the item cache to get the latest aggregate ratings

        // check no reviews should be returned in the items if item is not got by getDetails
        Item item = testDataProvider.getLayout("Game", null, 2).getItems().get(0);
        Assert.assertNull(item.getReviews());
        validationHelper.verifyAggregateRatings(item.getAggregatedRatings(), caseyAggregateRating);

        // verify the review & ratings in getDetails
        item = testDataProvider.getItemDetails(item.getSelf().getValue()).getItem();
        List<Review> reviews = fetchReviewsFromItemDetails(item);
        Assert.assertEquals(reviews.size(), numOfReviews, "Number of reviews not correct.");
        for (int i = 0;i < reviews.size(); ++i) {
            validationHelper.verifyReview(reviews.get(i), caseyReviews.get(i), userProfile.getNickName());
        }
        // verify the user review
        validationHelper.verifyReview(item.getCurrentUserReview(), caseyReviews.get(0), userProfile.getNickName());
        validationHelper.verifyAggregateRatings(item.getAggregatedRatings(), caseyAggregateRating);
    }

    @Test
    public void testGetReviewInvalidUserId() throws Exception {
        gotoToc();

        // prepare review with invalid user id
        List<CaseyReview> caseyReviews = new ArrayList<>();
        caseyReviews.add(DataGenerator.instance().generateCaseyReview(RandomStringUtils.randomAlphabetic(10)));
        testDataProvider.postCaseyEmulatorData(caseyReviews, null);
        testDataProvider.clearCache(); // clear the item cache to get the latest aggregate ratings

        // check no reviews should be returned in the items if item is not got by getDetails
        Item item = testDataProvider.getLayout("Game", null, 2).getItems().get(0);
        item = testDataProvider.getItemDetails(item.getSelf().getValue()).getItem();
        List<Review> reviews = fetchReviewsFromItemDetails(item);
        Assert.assertEquals(reviews.size(), 1, "Number of reviews not correct.");
        validationHelper.verifyReview(reviews.get(0), caseyReviews.get(0), null);

        testDataProvider.postCaseyEmulatorData(null, null);
        testDataProvider.clearCache();

    }

    private void testGetCategorySection(final String category, String sectionName, List<String> itemNames) throws Exception {
        int pageSize = 2;
        gotoToc();
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(category, null, pageSize);

        // verify the layout response
        Assert.assertFalse(sectionLayoutResponse.getOrdered());
        Assert.assertEquals(sectionLayoutResponse.getName(), sectionName);
        Assert.assertTrue(sectionLayoutResponse.getChildren().isEmpty());
        Assert.assertTrue(sectionLayoutResponse.getBreadcrumbs().isEmpty());
        Assert.assertTrue(!sectionLayoutResponse.getItems().isEmpty());

        Map<String, Item> nameToItems = new HashMap<>();
        List<Item> items = getItemsInSection(category, null, pageSize);

        for (Item item : items) {
            Assert.assertNotNull(org.apache.commons.collections.CollectionUtils.find(item.getAppDetails().getCategories(),
                    new Predicate() {
                        @Override
                        public boolean evaluate(Object object) {
                            return ((CategoryInfo) object).getName().equals(category);
                        }
                    }
            ), "category not found in items");
            nameToItems.put(item.getTitle(), item);
        }
        for (String itemName : itemNames) {
            Assert.assertTrue(nameToItems.containsKey(itemName), String.format("Item %s not found in category %s", itemName, category));
        }
    }

    private TocResponse gotoToc() throws Exception {
        // create user and sign in
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();
        testDataProvider.signIn(userName);

        // get toc
        TocResponse response = testDataProvider.getToc();
        validationHelper.verifyTocTosChallenge(response.getChallenge());

        // accept the tos
        AcceptTosResponse tosResponse = testDataProvider.acceptTos(response.getChallenge().getTos().getTosId());
        Assert.assertEquals(tosResponse.getTos(), response.getChallenge().getTos().getTosId());

        // get the first item in the first section
        return testDataProvider.getToc();
    }

    private void exploreSection(String category, String criteria) throws Exception {
        // get layout
        int pageSize = 2;
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(category, criteria, pageSize);
        if (sectionLayoutResponse.getNext() != null) { // get rest of the items by get list
            Assert.assertEquals(category, sectionLayoutResponse.getNext().getCategory());
            Assert.assertEquals(criteria, sectionLayoutResponse.getNext().getCriteria());
            listAllItems(category, criteria, pageSize, sectionLayoutResponse.getNext().getCursor());
        }

        // explore sub section
        if (!CollectionUtils.isEmpty(sectionLayoutResponse.getChildren())) {
            for (SectionInfo sectionInfo : sectionLayoutResponse.getChildren()) {
                exploreSection(sectionInfo.getCategory(), sectionInfo.getCriteria());
            }
        }
    }

    private void listAllItems(String category, String criteria, int pageSize, String cursor) throws Exception {
        int loopLimit = 1000;
        ListRequest listRequest = new ListRequest();
        listRequest.setCount(pageSize);
        listRequest.setCategory(category);
        listRequest.setCriteria(criteria);
        listRequest.setCursor(cursor);

        while (true) {
            if (loopLimit-- < 0) {
                Assert.fail("Loop limit reached in iterate list");
            }

            ListResponse result = testDataProvider.getList(listRequest);
            Assert.assertTrue(result.getItems().size() <= pageSize);

            if (result.getNext() == null || result.getItems().isEmpty()) {
                break;
            }

            Assert.assertEquals(result.getNext().getCategory(), category);
            Assert.assertEquals(result.getNext().getCriteria(), criteria);
            Assert.assertEquals(result.getNext().getCount().intValue(), 2);
            listRequest.setCursor(result.getNext().getCursor());
        }
    }

    private List<Item> getItemsInSection(String category, String criteria, Integer pageSize) throws Exception {
        List<Item> items = new ArrayList<>();
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(category, criteria, pageSize);
        items.addAll(sectionLayoutResponse.getItems());
        ListResponse.NextOption nextOption = sectionLayoutResponse.getNext();

        if (nextOption != null) {
            Assert.assertEquals(nextOption.getCategory(), category);
            Assert.assertEquals(nextOption.getCriteria(), criteria);
            Assert.assertEquals(nextOption.getCount(), pageSize);
        }

        while (nextOption != null) {
            ListResponse listResponse = testDataProvider.getList(nextOption.getCategory(), nextOption.getCriteria(), nextOption.getCursor(), nextOption.getCount());
            items.addAll(listResponse.getItems());
            if (listResponse.getItems().isEmpty()) {
                break;
            }
            nextOption = listResponse.getNext();

            if (nextOption != null) {
                Assert.assertEquals(nextOption.getCategory(), category);
                Assert.assertEquals(nextOption.getCriteria(), criteria);
                Assert.assertEquals(nextOption.getCount(), pageSize);
            }
        }

        return items;
    }

    private List<Review> fetchReviewsFromItemDetails(Item item) throws Exception {
        List<Review> result = new ArrayList<>();
        Assert.assertNotNull(item.getReviews(), "reviews in item details should not be null");
        ReviewsResponse reviewsResponse = item.getReviews();
        result.addAll(reviewsResponse.getReviews());
        while (reviewsResponse.getNext() != null &&  !CollectionUtils.isEmpty(reviewsResponse.getReviews())) {
            ReviewsResponse.NextOption next = reviewsResponse.getNext();
            reviewsResponse = testDataProvider.getReviews(item.getSelf(), next.getCursor(), next.getCount());
            result.addAll(reviewsResponse.getReviews());
            if (reviewsResponse.getNext() != null) {
                Assert.assertEquals(reviewsResponse.getNext().getItemId(), item.getSelf(), "itemId should be the same in get reviews");
                Assert.assertEquals(reviewsResponse.getNext().getCount(), next.getCount(), "itemId should be the same in get reviews");
                Assert.assertNotNull(reviewsResponse.getNext().getCursor(), "cursor should not be null");
            }
        }

        return result;
    }
}

