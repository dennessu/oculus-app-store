/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.util.IdFormatter;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.browse.document.*;
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating;
import com.junbo.store.spec.model.external.casey.CaseyReview;
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;
import com.junbo.store.spec.model.identity.StoreUserProfile;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.purchase.MakeFreePurchaseResponse;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.store.apihelper.TestContext;
import com.junbo.test.store.utility.DataGenerator;
import com.junbo.test.store.utility.StoreBrowseValidationHelper;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.RandomStringUtils;
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

    private boolean verifyAllItemDetailsInExplore = false;

    public enum GetItemMethod {
        List,
        Library,
        Purchase,
        Details
    }

    private StoreBrowseValidationHelper storeBrowseValidationHelper;

    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        if (serviceClientEnabled) {
            oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
        }
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOG);
        storeBrowseValidationHelper = new StoreBrowseValidationHelper(testDataProvider);

        if (ConfigHelper.getSetting("explore.items.details.verifyall") != null) {
            verifyAllItemDetailsInExplore = Boolean.valueOf(ConfigHelper.getSetting("explore.items.details.verifyall"));
        }
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
            verifyItem(item, GetItemMethod.Library, null);
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
        verifyItem(response.getEntitlements().get(0).getItemDetails(), GetItemMethod.Purchase, null);

        LibraryResponse libraryResponse = testDataProvider.getLibrary();
        assert libraryResponse.getItems().size() == 1;
        ItemId itemId = libraryResponse.getItems().get(0).getSelf();

        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId.getValue());
        verifyItem(response.getEntitlements().get(0).getItemDetails(), GetItemMethod.Details, true);
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
        verifyItem(detailsResponse.getItem(), GetItemMethod.Details, false);
    }

    @Test
    public void testGetDeliveryVersionCodeNotFound() throws Exception {
        // create user and sign in
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();
        testDataProvider.signIn(userName);

        // buy offers
        String offerId;
        if (offer_iap_free.toLowerCase().contains("test")) {
            offerId = testDataProvider.getOfferIdByName(offer_digital_free);
        } else {
            offerId = offer_digital_free;
        }
        MakeFreePurchaseResponse response = testDataProvider.makeFreePurchase(offerId, null);
        response = testDataProvider.makeFreePurchase(offerId, response.getChallenge().getTos().getTosId());
        ItemId itemId = response.getEntitlements().get(0).getItem();
        // get delivery
        testDataProvider.getDelivery(itemId, Integer.MAX_VALUE, 412);
        assert Master.getInstance().getApiErrorMsg().contains("130.118");
    }

    @Test
    public void testGetDetailsNotAvailableInCountry() throws Exception {
        gotoToc();
        TestContext.getData().putHeader("oculus-geoip-country-code", "CN");
        com.junbo.catalog.spec.model.item.Item item = testDataProvider.getItemByName(item_digital_oculus_free1);
        // get the item details
        testDataProvider.getItemDetails(item.getItemId(), 404);
        assert Master.getInstance().getApiErrorMsg().contains("130.004");
    }

    @Test
    public void testGetDetailsNotExist() throws Exception {
        gotoToc();
        // get the item details
         testDataProvider.getItemDetails("noexists", 404);
        assert Master.getInstance().getApiErrorMsg().contains("130.004");
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
        CmsPage cmsPage =  createCmsPageAndOffers(Arrays.asList("slot1", "slot2"), Arrays.asList("test slot1", "test slot2"),
                Arrays.asList(getOfferIds(cmsSlot1Items), getOfferIds(cmsSlot2Items)));

        int pageSize = 2;
        TocResponse tocResponse = gotoToc();
        SectionInfo featuredSectionInfo = tocResponse.getSections().get(0).toSectionInfo();
        Assert.assertEquals(featuredSectionInfo.getCriteria(), featureRootCriteria, "section criteria not match");
        storeBrowseValidationHelper.validateCmsSection(tocResponse.getSections().get(0).getChildren().get(0), cmsPage, "slot1");
        storeBrowseValidationHelper.validateCmsSection(tocResponse.getSections().get(0).getChildren().get(1), cmsPage, "slot2");

        // validate top level feature section layout
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(featuredSectionInfo.getCategory(), featuredSectionInfo.getCriteria(), pageSize);
        storeBrowseValidationHelper.validateCmsTopLevelSectionLayout(sectionLayoutResponse, 2, "Featured");

        SectionInfo slot1Section = sectionLayoutResponse.getChildren().get(0);
        SectionInfo slot2Section = sectionLayoutResponse.getChildren().get(1);
        storeBrowseValidationHelper.validateCmsSection(slot1Section, cmsPage, "slot1");
        storeBrowseValidationHelper.validateCmsSection(slot2Section, cmsPage, "slot2");


        // get slot1 section
        SectionLayoutResponse slot1Layout = testDataProvider.getLayout(null, slot1Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifySectionLayoutBreadcrumbs(slot1Layout, sectionLayoutResponse, featuredSectionInfo);
        storeBrowseValidationHelper.validateCmsSection(slot1Layout, cmsPage, "slot1", pageSize, true);
        Assert.assertTrue(slot1Layout.getChildren().isEmpty(), "children should be empty");

        List<Item> items = getItemsInSection(slot1Section.getCategory(), slot1Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifyItemsInList(cmsSlot1Items, items, true);

        // get feature-offers section
        SectionLayoutResponse slot2Layout = testDataProvider.getLayout(null, slot2Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifySectionLayoutBreadcrumbs(slot2Layout, sectionLayoutResponse, featuredSectionInfo);
        storeBrowseValidationHelper.validateCmsSection(slot2Layout, cmsPage, "slot2", pageSize, true);
        Assert.assertTrue(slot2Layout.getChildren().isEmpty(), "children should be empty");
        items = getItemsInSection(slot2Section.getCategory(), slot2Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifyItemsInList(cmsSlot2Items, items, true);
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
        if (useCaseyEmulator) {
            createCmsPageAndOffers(Arrays.asList("slot1", "slot2"), Arrays.asList("test slot1", "test slot2"),
                    Arrays.asList(getOfferIds(cmsSlot1Items), getOfferIds(cmsSlot1Items)));
        }

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
        testDataProvider.postCaseyEmulatorData(caseyReviews, caseyAggregateRating, null);
        testDataProvider.clearCache(); // clear the item cache to get the latest aggregate ratings

        // check no reviews should be returned in the items if item is not got by getDetails
        Item item = testDataProvider.getLayout("Game", null, 2).getItems().get(0);
        Assert.assertNull(item.getReviews());
        storeBrowseValidationHelper.verifyAggregateRatings(item.getAggregatedRatings(), caseyAggregateRating);

        // verify the review & ratings in getDetails
        item = testDataProvider.getItemDetails(item.getSelf().getValue()).getItem();
        List<Review> reviews = fetchReviewsFromItemDetails(item);
        Assert.assertEquals(reviews.size(), numOfReviews, "Number of reviews not correct.");
        for (int i = 0;i < reviews.size(); ++i) {
            storeBrowseValidationHelper.verifyReview(reviews.get(i), caseyReviews.get(i), userProfile.getNickName());
        }
        // verify the user review
        storeBrowseValidationHelper.verifyReview(item.getCurrentUserReview(), caseyReviews.get(0), userProfile.getNickName());
        storeBrowseValidationHelper.verifyAggregateRatings(item.getAggregatedRatings(), caseyAggregateRating);
    }

    @Test
    public void testGetReviewInvalidUserId() throws Exception {
        gotoToc();

        // prepare review with invalid user id
        List<CaseyReview> caseyReviews = new ArrayList<>();
        caseyReviews.add(DataGenerator.instance().generateCaseyReview(RandomStringUtils.randomAlphabetic(10)));
        testDataProvider.postCaseyEmulatorData(caseyReviews, null, null);
        testDataProvider.clearCache(); // clear the item cache to get the latest aggregate ratings

        // check no reviews should be returned in the items if item is not got by getDetails
        Item item = testDataProvider.getLayout("Game", null, 2).getItems().get(0);
        item = testDataProvider.getItemDetails(item.getSelf().getValue()).getItem();
        List<Review> reviews = fetchReviewsFromItemDetails(item);
        Assert.assertEquals(reviews.size(), 1, "Number of reviews not correct.");
        storeBrowseValidationHelper.verifyReview(reviews.get(0), caseyReviews.get(0), null);

        testDataProvider.postCaseyEmulatorData(null, null, null);
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

    private int exploreSection(String category, String criteria) throws Exception {
        // get layout
        LOGGER.info("name=ExploreSectionStart, category={}, criteria={}", category, criteria);
        int numOfItems = 0;
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(category, criteria, listItemPageSize);
        numOfItems += sectionLayoutResponse.getItems().size();
        verifyItemsInExplore(sectionLayoutResponse.getItems());
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

            ListResponse result = testDataProvider.getList(listRequest);
            count += result.getItems().size();
            Assert.assertTrue(result.getItems().size() <= pageSize);
            verifyItemsInExplore(result.getItems());

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

    private void verifyItem(Item item, GetItemMethod method, Boolean ownedByUser) throws Exception {
        storeBrowseValidationHelper.verifyItem(item, serviceClientEnabled);
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

    private List<OfferId> getOfferIds(List<String> itemNames) throws Exception {
        List<OfferId> offerIds = new ArrayList<>();
        for(String name : itemNames) {
            com.junbo.catalog.spec.model.item.Item item = testDataProvider.getItemByName(name);
            com.junbo.catalog.spec.model.offer.Offer offer = testDataProvider.getOfferByItem(item.getItemId());
            offerIds.add(new OfferId(offer.getOfferId()));
        }
        return offerIds;
    }

    private CmsPage createCmsPageAndOffers(List<String> slots, List<String> slotDescription, List<List<OfferId>> offers) throws Exception {
        CmsPage cmsPage =  DataGenerator.instance().genCmsPage(cmsPagePath);
        cmsPage.setSlots(new TreeMap<String, CmsContentSlot>());
        Map<String, List<OfferId>> cmsOffers = new HashMap<>();
        for (int i = 0;i < slots.size();++i) {
            CmsContentSlot cmsContentSlot = new CmsContentSlot();
            cmsContentSlot.setDescription(slotDescription.get(i));
            cmsPage.getSlots().put(slots.get(i), cmsContentSlot);
            cmsOffers.put(cmsPage.getPath() + "-" + slots.get(i), offers.get(i));
        }
        testDataProvider.postCaseyEmulatorData(cmsPage, cmsOffers);
        return cmsPage;
    }

    private void verifyItemsInExplore(List<Item> items) throws Exception {
        int selected = items.isEmpty() ? -1 : DataGenerator.instance().random().nextInt(items.size());
        for (int i = 0; i < items.size(); ++i) {
            if (verifyAllItemDetailsInExplore || i == selected) {
                Item item = items.get(i);
                LOGGER.info("name=Verify_Item_InExplore, item={}", item.getSelf().getValue());
                verifyItem(item, GetItemMethod.List, null);
                LOGGER.info("name=Get_And_Verify_ItemDetail_InExplore, item={}", item.getSelf().getValue());
                verifyItem(testDataProvider.getItemDetails(item.getSelf().getValue()).getItem(), GetItemMethod.Details, false);
            }
        }
    }
}


