/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.catalog.spec.model.offer.*;
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
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.store.utility.DataGenerator;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

/**
 * The StoreBrowseTesting class.
 */
public class StoreBrowseTesting extends BaseTestClass {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreBrowseTesting.class);

    public enum GetItemMethod {
        List,
        Library,
        Purchase,
        Details
    }

    @BeforeClass
    public void setUp() throws Exception {
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
    }

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
        assert libraryResponse.getItems().size() == 1;
        for (Item item: libraryResponse.getItems()) {
            verifyItem(item, true, GetItemMethod.Library, null);
        }
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
        verifyItem(response.getEntitlements().get(0).getItemDetails(), true, GetItemMethod.Purchase, null);

        LibraryResponse libraryResponse = testDataProvider.getLibrary();
        assert libraryResponse.getItems().size() == 1;
        ItemId itemId = libraryResponse.getItems().get(0).getSelf();

        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId.getValue());
        verifyItem(response.getEntitlements().get(0).getItemDetails(), true, GetItemMethod.Details, true);
        Assert.assertTrue(detailsResponse.getItem().getOwnedByCurrentUser());
        Assert.assertTrue(detailsResponse.getItem().getOffer().getIsFree());


        // get delivery
        DeliveryResponse deliveryResponse = testDataProvider.getDelivery(itemId);
        Assert.assertTrue(deliveryResponse.getDownloadUrl() != null);
    }

    @Test
    public void testGetDetails() throws Exception {
        gotoToc();
        String offerId = testDataProvider.getOfferIdByName(offer_digital_oculus_free1);
        com.junbo.catalog.spec.model.offer.Offer catalogOffer = offerService.getOffer(offerId);
        OfferRevision offerRevision = offerRevisionService.getOfferRevision(catalogOffer.getCurrentRevisionId());
        Assert.assertEquals(offerRevision.getItems().size(), 1);
        String itemId = offerRevision.getItems().get(0).getItemId();

        // get the item details
        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId);
        verifyItem(detailsResponse.getItem(), true, GetItemMethod.Details, false);
    }

    @Test
    public void testGetList() throws Exception {
        // get the first item in the first section
        TocResponse response = gotoToc();
        SectionInfoNode sectionInfo = response.getSections().get(0);
        listAllItems(sectionInfo.getCategory(), sectionInfo.getCriteria(), 2, null);
    }

    @Property(
            priority = Priority.BVT,
            features = "Store browse",
            component = Component.STORE,
            owner = "fzhang",
            status = Status.Enable,
            environment = "release",
            description = "Test get all the items under all the section"
    )
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
    @Property(
            priority = Priority.BVT,
            features = "Store browse",
            component = Component.STORE,
            owner = "fzhang",
            status = Status.Enable,
            environment = "release",
            description = "Test the feature sections"
    )
    public void testGetFeaturedSectionSmoke() throws Exception {
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
        Assert.assertTrue(sectionLayoutResponse.getChildren().size() > 0, "Child under feature section is empty");

        for (SectionInfo sectionInfo : sectionLayoutResponse.getChildren()) {
            exploreSection(sectionInfo.getCategory(), sectionInfo.getCriteria());
        }
    }

    @Test
    public void testGetGameSection() throws Exception {
        testGetCategorySection("Game", "Game", Arrays.asList(item_digital_normal1, item_digital_oculus_free1, item_digital_free));
    }

    @Test
    public void testGetAppSection() throws Exception {
        testGetCategorySection("App", "App", Arrays.asList(item_digital_normal2, item_digital_oculus_free2));
    }

    @Test
    public void testGetReviewAndAggregateRating() throws Exception {
        gotoToc();
        StoreUserProfile userProfile = testDataProvider.getUserProfile().getUserProfile();

        // prepare review & aggregate ratings
        int numOfReviews = 20;
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
            validationHelper.verifyReview(reviews.get(i), caseyReviews.get(i), userProfile);
        }
        // verify the user review
        validationHelper.verifyReview(item.getCurrentUserReview(), caseyReviews.get(0), userProfile);
        validationHelper.verifyAggregateRatings(item.getAggregatedRatings(), caseyAggregateRating);
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

    private int exploreSection(String category, String criteria) throws Exception {
        // get layout
        LOGGER.info("name=ExploreSectionStart, category={}, criteria={}", category, criteria);
        int numOfItems = 0;
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(category, criteria, listItemPageSize);
        numOfItems += sectionLayoutResponse.getItems().size();
        for (Item item : sectionLayoutResponse.getItems()) {
            verifyItem(item, false, GetItemMethod.List, null);
            verifyItem(testDataProvider.getItemDetails(item.getSelf().getValue()).getItem(), false, GetItemMethod.Details, false);
        }
        if (sectionLayoutResponse.getNext() != null) { // get rest of the items by get list
            Assert.assertEquals(category, sectionLayoutResponse.getNext().getCategory());
            Assert.assertEquals(criteria, sectionLayoutResponse.getNext().getCriteria());
            numOfItems += listAllItems(category, criteria, listItemPageSize, sectionLayoutResponse.getNext().getCursor());
        }

        // explore sub section
        if (!CollectionUtils.isEmpty(sectionLayoutResponse.getChildren())) {
            for (SectionInfo sectionInfo : sectionLayoutResponse.getChildren()) {
                exploreSection(sectionInfo.getCategory(), sectionInfo.getCriteria());
            }
        }
        LOGGER.info("name=ExploreSectionEnd, category={}, criteria={}, numOfItem={}", category, criteria, numOfItems);
        return numOfItems;
    }

    private int listAllItems(String category, String criteria, int pageSize, String cursor) throws Exception {
        int count = 0;
        ListRequest listRequest = new ListRequest();
        listRequest.setCount(pageSize);
        listRequest.setCategory(category);
        listRequest.setCriteria(criteria);
        listRequest.setCursor(cursor);

        while (true) {

            ListResponse result = testDataProvider.getList(listRequest, browseRetryCount);
            count += result.getItems().size();
            Assert.assertTrue(result.getItems().size() <= pageSize);
            for (Item item : result.getItems()) {
                verifyItem(item, false, GetItemMethod.List, null);
                verifyItem(testDataProvider.getItemDetails(item.getSelf().getValue()).getItem(), false, GetItemMethod.Details, false);
            }

            if (result.getNext() == null || result.getItems().isEmpty()) {
                break;
            }

            Assert.assertEquals(result.getNext().getCategory(), category);
            Assert.assertEquals(result.getNext().getCriteria(), criteria);
            Assert.assertEquals(result.getNext().getCount().intValue(), pageSize);
            listRequest.setCursor(result.getNext().getCursor());
        }
        return count;
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
            ListResponse listResponse = testDataProvider.getList(nextOption.getCategory(), nextOption.getCriteria(), nextOption.getCursor(), nextOption.getCount(), browseRetryCount);
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

    private void verifyItem(Item item, boolean forceVerify, GetItemMethod method, Boolean ownedByUser) throws Exception {
        if (!forceVerify && (!itemsToVerify.containsKey(item.getTitle())) && (!itemsToVerify.containsKey(item.getSelf().getValue()))) {
            return ;
        }
        validationHelper.verifyItem(item, itemsToVerify.get(item.getTitle()));
        if (method == GetItemMethod.Details) {
            Assert.assertEquals(item.getOwnedByCurrentUser().booleanValue(), ownedByUser.booleanValue());
        } else if (method == GetItemMethod.Library || method == GetItemMethod.Purchase) {
            Assert.assertNull(item.getReviews());
            Assert.assertNull(item.getCurrentUserReview());
            Assert.assertTrue(item.getOwnedByCurrentUser());
        } else if (method == GetItemMethod.List) {
            Assert.assertNull(item.getReviews());
            Assert.assertNull(item.getCurrentUserReview());
            Assert.assertNull(item.getOwnedByCurrentUser());
        }
    }
}

