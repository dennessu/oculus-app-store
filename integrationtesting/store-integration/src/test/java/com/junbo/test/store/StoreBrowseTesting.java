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
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign;
import com.junbo.store.spec.model.external.casey.cms.CmsPage;
import com.junbo.store.spec.model.external.casey.cms.CmsSchedule;
import com.junbo.store.spec.model.external.casey.cms.Placement;
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

    private String caseyCmsPagePath;

    private String caseyCmsPageLabel;

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

        if (ConfigHelper.getSetting("casey.emulator.cmspage.path") != null) {
            caseyCmsPagePath = ConfigHelper.getSetting("casey.emulator.cmspage.path");
        }

        if (ConfigHelper.getSetting("casey.emulator.cmspage.label") != null) {
            caseyCmsPageLabel = ConfigHelper.getSetting("casey.emulator.cmspage.label");
        }

    }

    @Test
    public void testGetToc() throws Exception {
        // create user and sign in
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();
        testDataProvider.signIn(createUserRequest.getEmail());

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
        testDataProvider.signIn(createUserRequest.getEmail());

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
        testDataProvider.signIn(createUserRequest.getEmail());

        // call get library and expect empty items
        assert testDataProvider.getLibrary().getItems().isEmpty();

        // buy offers
        com.junbo.catalog.spec.model.item.Item item = testDataProvider.getItemByName(item_digital_oculus_free1);
        String offerId = testDataProvider.getOfferByItem(item.getItemId()).getOfferId();
        MakeFreePurchaseResponse response = testDataProvider.makeFreePurchase(offerId, null);
        response = testDataProvider.makeFreePurchase(offerId, response.getChallenge().getTos().getTosId());
        Assert.assertNotNull(response.getEntitlements().get(0).getItemDetails(), "itemDetails in entitlement should not be null");
        verifyItem(response.getEntitlements().get(0).getItemDetails(), GetItemMethod.Purchase, null);

        LibraryResponse libraryResponse = testDataProvider.getLibrary();
        assert libraryResponse.getItems().size() == 1;
        ItemId itemId = libraryResponse.getItems().get(0).getSelf();
        Assert.assertNotNull(libraryResponse.getItems().get(0).getOffer());

        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId.getValue());
        verifyItem(response.getEntitlements().get(0).getItemDetails(), GetItemMethod.Details, true);
        Assert.assertTrue(detailsResponse.getItem().getOwnedByCurrentUser());
        Assert.assertTrue(detailsResponse.getItem().getOffer().getIsFree());

        // get delivery
        DeliveryResponse deliveryResponse = testDataProvider.getDelivery(itemId);
        Assert.assertTrue(deliveryResponse.getDownloadUrl() != null);

        // switch to country that does not purchasable and get library, verify the offer is null
        TestContext.getData().putHeader("oculus-geoip-country-code", "CN");
        libraryResponse = testDataProvider.getLibrary();
        assert libraryResponse.getItems().size() == 1;
        Assert.assertNull(libraryResponse.getItems().get(0).getOffer());
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
    public void testGetDetailsLocalesFallback() throws Exception {
        gotoToc();
        String offerId = testDataProvider.getOfferIdByName(offer_digital_oculus_free1);
        com.junbo.catalog.spec.model.offer.Offer catalogOffer = offerService.getOffer(offerId);
        OfferRevision offerRevision = offerRevisionService.getOfferRevision(catalogOffer.getCurrentRevisionId());
        Assert.assertEquals(offerRevision.getItems().size(), 1);
        String itemId = offerRevision.getItems().get(0).getItemId();

        // get the item details
        TestContext.getData().putHeader("Accept-Language", "zh-CN");
        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId);
        verifyItem(detailsResponse.getItem(), GetItemMethod.Details, false);
    }

    @Test
    public void testGetDeliveryVersionCodeNotFound() throws Exception {
        // create user and sign in
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();
        testDataProvider.signIn(createUserRequest.getEmail());

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
        setupCmsPageAndOffers(caseyCmsPagePath, caseyCmsPageLabel, Arrays.asList("slot1", "slot2"), Arrays.asList("test slot1", "test slot2"),
                Arrays.asList(getOfferIds(cmsSlot1Items), getOfferIds(cmsSlot2Items)));

        int pageSize = 2;
        TocResponse tocResponse = gotoToc();
        SectionInfo featuredSectionInfo = tocResponse.getSections().get(0).toSectionInfo();
        Assert.assertEquals(featuredSectionInfo.getCriteria(), featureRootCriteria, "section criteria not match");
        storeBrowseValidationHelper.validateCmsSection(tocResponse.getSections().get(0).getChildren().get(0), "test slot1", cmsPageName, "slot1");
        storeBrowseValidationHelper.validateCmsSection(tocResponse.getSections().get(0).getChildren().get(1), "test slot2", cmsPageName, "slot2");

        // validate top level feature section layout
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(featuredSectionInfo.getCategory(), featuredSectionInfo.getCriteria(), pageSize);
        storeBrowseValidationHelper.validateCmsTopLevelSectionLayout(sectionLayoutResponse, 2, "Featured");
        storeBrowseValidationHelper.getAndValidateItemList(sectionLayoutResponse.getCategory(), sectionLayoutResponse.getCriteria(), null, pageSize, 0, false);

        SectionInfo slot1Section = sectionLayoutResponse.getChildren().get(0);
        SectionInfo slot2Section = sectionLayoutResponse.getChildren().get(1);
        storeBrowseValidationHelper.validateCmsSection(slot1Section, "test slot1", cmsPageName, "slot1");
        storeBrowseValidationHelper.validateCmsSection(slot2Section, "test slot2", cmsPageName, "slot2");


        // get slot1 section
        SectionLayoutResponse slot1Layout = testDataProvider.getLayout(null, slot1Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifySectionLayoutBreadcrumbs(slot1Layout, sectionLayoutResponse, featuredSectionInfo);
        storeBrowseValidationHelper.validateCmsSection(slot1Layout, "test slot1", pageSize, true);
        storeBrowseValidationHelper.getAndValidateItemList(slot1Layout.getCategory(), slot1Layout.getCriteria(), null, pageSize, pageSize, true);
        Assert.assertTrue(slot1Layout.getChildren().isEmpty(), "children should be empty");

        List<Item> items = getItemsInSection(slot1Section.getCategory(), slot1Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifyItemsInList(cmsSlot1Items, items, true);

        // get feature-offers section
        SectionLayoutResponse slot2Layout = testDataProvider.getLayout(null, slot2Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifySectionLayoutBreadcrumbs(slot2Layout, sectionLayoutResponse, featuredSectionInfo);
        storeBrowseValidationHelper.validateCmsSection(slot2Layout, "test slot2", pageSize, true);
        storeBrowseValidationHelper.getAndValidateItemList(slot2Layout.getCategory(), slot2Layout.getCriteria(), null, pageSize, pageSize, true);
        Assert.assertTrue(slot2Layout.getChildren().isEmpty(), "children should be empty");
        items = getItemsInSection(slot2Section.getCategory(), slot2Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifyItemsInList(cmsSlot2Items, items, true);
    }

    @Test
    public void testGetFeaturedSectionWithLocaleFallback() throws Exception {
        setupCmsPageAndOffers(caseyCmsPagePath, caseyCmsPageLabel, Arrays.asList("slot1", "slot2"), Arrays.asList("test slot1", "test slot2"),
                Arrays.asList(getOfferIds(cmsSlot1Items), getOfferIds(cmsSlot2Items)));

        TestContext.getData().putHeader("Accept-Language", "zh-CN");
        int pageSize = 2;
        TocResponse tocResponse = gotoToc();
        SectionInfo featuredSectionInfo = tocResponse.getSections().get(0).toSectionInfo();
        Assert.assertEquals(featuredSectionInfo.getCriteria(), featureRootCriteria, "section criteria not match");
        storeBrowseValidationHelper.validateCmsSection(tocResponse.getSections().get(0).getChildren().get(0), "test slot1", cmsPageName, "slot1");
        storeBrowseValidationHelper.validateCmsSection(tocResponse.getSections().get(0).getChildren().get(1), "test slot2", cmsPageName, "slot2");

        // validate top level feature section layout
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(featuredSectionInfo.getCategory(), featuredSectionInfo.getCriteria(), pageSize);
        storeBrowseValidationHelper.validateCmsTopLevelSectionLayout(sectionLayoutResponse, 2, "Featured");
        storeBrowseValidationHelper.getAndValidateItemList(sectionLayoutResponse.getCategory(), sectionLayoutResponse.getCriteria(), null, pageSize, 0, false);

        SectionInfo slot1Section = sectionLayoutResponse.getChildren().get(0);
        SectionInfo slot2Section = sectionLayoutResponse.getChildren().get(1);
        storeBrowseValidationHelper.validateCmsSection(slot1Section, "test slot1", cmsPageName, "slot1");
        storeBrowseValidationHelper.validateCmsSection(slot2Section, "test slot2", cmsPageName, "slot2");

        // get slot1 section
        SectionLayoutResponse slot1Layout = testDataProvider.getLayout(null, slot1Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifySectionLayoutBreadcrumbs(slot1Layout, sectionLayoutResponse, featuredSectionInfo);
        storeBrowseValidationHelper.validateCmsSection(slot1Layout, "test slot1", pageSize, true);
        storeBrowseValidationHelper.getAndValidateItemList(slot1Layout.getCategory(), slot1Layout.getCriteria(), null, pageSize, pageSize, true);

        // get feature-offers section
        SectionLayoutResponse slot2Layout = testDataProvider.getLayout(null, slot2Section.getCriteria(), pageSize);
        storeBrowseValidationHelper.verifySectionLayoutBreadcrumbs(slot2Layout, sectionLayoutResponse, featuredSectionInfo);
        storeBrowseValidationHelper.validateCmsSection(slot2Layout, "test slot2", pageSize, true);
        storeBrowseValidationHelper.getAndValidateItemList(slot2Layout.getCategory(), slot2Layout.getCriteria(), null, pageSize, pageSize, true);
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
            setupCmsPageAndOffers(caseyCmsPagePath, caseyCmsPageLabel, Arrays.asList("slot1", "slot2"), Arrays.asList("test slot1", "test slot2"),
                    Arrays.asList(getOfferIds(cmsSlot1Items), getOfferIds(cmsSlot1Items)));
        }

        int pageSize = 2;
        TocResponse tocResponse = gotoToc();
        SectionInfo featuredSectionInfo = tocResponse.getSections().get(0).toSectionInfo();
        Assert.assertEquals(featuredSectionInfo.getCriteria(), featureRootCriteria, "section criteria not match");

        // validate top level feature section layout
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(featuredSectionInfo.getCategory(), featuredSectionInfo.getCriteria(), pageSize);
        Assert.assertTrue(sectionLayoutResponse.getBreadcrumbs().isEmpty(), "top level section's breadcrumbs should be empty");
        Assert.assertTrue(sectionLayoutResponse.getChildren().size() > 0);
        Assert.assertEquals(sectionLayoutResponse.getName(), "Featured");
        storeBrowseValidationHelper.getAndValidateItemList(sectionLayoutResponse.getCategory(), sectionLayoutResponse.getCriteria(), null, pageSize, 0, false);
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
        Item item = testDataProvider.getList("Game", null, null, 2).getItems().get(0);
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
        Item item = testDataProvider.getList("Game", null, null, 2).getItems().get(0);
        item = testDataProvider.getItemDetails(item.getSelf().getValue()).getItem();
        List<Review> reviews = fetchReviewsFromItemDetails(item);
        Assert.assertEquals(reviews.size(), 1, "Number of reviews not correct.");
        storeBrowseValidationHelper.verifyReview(reviews.get(0), caseyReviews.get(0), null);

        testDataProvider.postCaseyEmulatorData((List<CaseyReview> )null, null, null);
        testDataProvider.clearCache();

    }

    @Test
    @Property(
            priority = Priority.BVT,
            features = "Store browse",
            component = Component.STORE,
            owner = "fzhang",
            status = Status.Enable,
            environment = "release",
            description = "Test the add review function"
    )
    public void testAddReview() throws Exception {
        gotoToc();
        StoreUserProfile userProfile = testDataProvider.getUserProfile().getUserProfile();

        // get item
        String offerId;
        String itemId;
        if (offer_digital_free.toLowerCase().contains("test")) {
            offerId = testDataProvider.getOfferIdByName(offer_digital_free);
        } else {
            offerId = offer_digital_free;
        }
        com.junbo.catalog.spec.model.offer.Offer offer = testDataProvider.getOfferByOfferId(offerId);
        OfferRevision offerRevision = testDataProvider.getOfferRevision(offer.getCurrentRevisionId());
        itemId = testDataProvider.getItemByItemId(offerRevision.getItems().get(0).getItemId()).getItemId();

        // add review not purchased
        AddReviewRequest addReviewRequest = DataGenerator.instance().generateAddReviewRequest(new ItemId(itemId));
        testDataProvider.addReview(addReviewRequest, 412);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.120"));
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("Could not review an item that not purchased."));

        // purchase & add again
        testDataProvider.makeFreePurchase(offerId, null, 200);
        AddReviewResponse reviewResponse = testDataProvider.addReview(addReviewRequest, 200);
        storeBrowseValidationHelper.validateAddReview(addReviewRequest, reviewResponse.getReview(), userProfile.getNickName());

        // validate with current user review
        Item item = testDataProvider.getItemDetails(itemId).getItem();
        storeBrowseValidationHelper.validateAddReview(addReviewRequest, item.getCurrentUserReview(), userProfile.getNickName());

        // add again should fail
        testDataProvider.addReview(addReviewRequest, 412);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.119"));
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("Review already exists."));
    }

    @Test
    public void testAddReviewInvalidRequest() throws Exception {
        // ratings missing
        gotoToc();
        Item item = testDataProvider.getList("Game", null, null, 2).getItems().get(0);

        AddReviewRequest addReviewRequest = DataGenerator.instance().generateAddReviewRequest(item.getSelf());

        // item empty
        addReviewRequest.setItemId(null);
        testDataProvider.addReview(addReviewRequest, 400);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.001"));

        // start ratings empty
        addReviewRequest = DataGenerator.instance().generateAddReviewRequest(item.getSelf());
        addReviewRequest.setStarRatings(null);
        testDataProvider.addReview(addReviewRequest, 400);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.001"));

        // invalid type
        addReviewRequest = DataGenerator.instance().generateAddReviewRequest(item.getSelf());
        addReviewRequest.setStarRatings(Collections.singletonMap("test", 1));
        testDataProvider.addReview(addReviewRequest, 400);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.001"));
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("invalid rating type: test"));

        // sore invalid
        addReviewRequest = DataGenerator.instance().generateAddReviewRequest(item.getSelf());
        addReviewRequest.setStarRatings(Collections.singletonMap("quality", 6));
        testDataProvider.addReview(addReviewRequest, 400);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.001"));
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("value must in range [1,5]"));
    }


    @Test
    public void testAcceptLanguageHeader() throws Exception {
        gotoToc();
        String offerId = testDataProvider.getOfferIdByName(offer_digital_oculus_free1);
        com.junbo.catalog.spec.model.offer.Offer catalogOffer = offerService.getOffer(offerId);
        OfferRevision offerRevision = offerRevisionService.getOfferRevision(catalogOffer.getCurrentRevisionId());
        Assert.assertEquals(offerRevision.getItems().size(), 1);
        String itemId = offerRevision.getItems().get(0).getItemId();

        // locale not found, fall back to en_US
        TestContext.getData().putHeader("Accept-Language", "en");
        DetailsResponse detailsResponse = testDataProvider.getItemDetails(itemId);
        verifyItem(detailsResponse.getItem(), GetItemMethod.Details, false);

        // wildcard locale
        TestContext.getData().putHeader("Accept-Language", "*");
        detailsResponse = testDataProvider.getItemDetails(itemId);
        verifyItem(detailsResponse.getItem(), GetItemMethod.Details, false);

        // invalid Accept-Language format, fall back to en_US
        TestContext.getData().putHeader("Accept-Language", "en_US");
        detailsResponse = testDataProvider.getItemDetails(itemId);
        verifyItem(detailsResponse.getItem(), GetItemMethod.Details, false);
    }

    @Test
    public void testInvalidAndroidId() throws Exception { // API does not fail even the android id is invalid
        TestContext.getData().putHeader("X-ANDROID-ID", "1233a2azzdfasdd22addda22");
        gotoToc();
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
        Assert.assertTrue(!testDataProvider.getList(sectionLayoutResponse.getCategory(), sectionLayoutResponse.getCriteria(), null, pageSize).getItems().isEmpty());

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
        testDataProvider.signIn(createUserRequest.getEmail());

        // get toc
        TocResponse response = testDataProvider.getToc();
        if (!tosDisabled) {
            validationHelper.verifyTocTosChallenge(response.getChallenge());
            // accept the tos
            AcceptTosResponse tosResponse = testDataProvider.acceptTos(response.getChallenge().getTos().getTosId());
            Assert.assertEquals(tosResponse.getTos(), response.getChallenge().getTos().getTosId());
        }

        // get the first item in the first section
        return testDataProvider.getToc();
    }

    private int exploreSection(String category, String criteria) throws Exception {
        // get layout
        LOGGER.info("name=ExploreSectionStart, category={}, criteria={}", category, criteria);
        int numOfItems = 0;
        SectionLayoutResponse sectionLayoutResponse = testDataProvider.getLayout(category, criteria, listItemPageSize);
        Assert.assertEquals(sectionLayoutResponse.getCategory(), category);
        Assert.assertEquals(sectionLayoutResponse.getCriteria(), criteria);

        numOfItems += listAllItems(category, criteria, listItemPageSize, null);

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
        String cursor = null;
        while (true) {
            ListResponse listResponse = testDataProvider.getList(category, criteria, cursor, pageSize);
            items.addAll(listResponse.getItems());

            ListResponse.NextOption nextOption = listResponse.getNext();
            if (nextOption != null) {
                Assert.assertEquals(nextOption.getCategory(), category);
                Assert.assertEquals(nextOption.getCriteria(), criteria);
                Assert.assertEquals(nextOption.getCount(), pageSize);
                Assert.assertNotNull(nextOption.getCursor());
                cursor = nextOption.getCursor();
            }

            if (listResponse.getItems().isEmpty() || nextOption == null) {
                break;
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

    private void setupCmsPageAndOffers(String path, String label, List<String> slots, List<String> names, List<List<OfferId>> offers) throws Exception {
        Map<String, List<OfferId>> cmsOffers = new HashMap<>();
        CmsPage cmsPage = DataGenerator.instance().genCmsPage(path, label, slots);

        CmsSchedule cmsSchedule = DataGenerator.instance().genCmsSchedule(cmsPage.getSelf().getId());
        for (int i = 0;i < slots.size();++i) {
            cmsSchedule.getSlots().put(slots.get(i), DataGenerator.instance().genCmsScheduleContent("header", names.get(i)));
            cmsOffers.put(cmsPageName + "-" + slots.get(i), offers.get(i));
        }

        testDataProvider.postCaseyEmulatorData(Arrays.asList(cmsSchedule), Arrays.asList(cmsPage), cmsOffers);
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

    private String getName(CmsCampaign cmsCampaign, String slot) {
        Placement placement = null;
        for (Placement e : cmsCampaign.getPlacements()) {
            if (e.getSlot().equals(slot)) {
                placement = e;
                break;
            }
        }
        return placement.getContent().getContents().get("category").getStrings().get(0).getLocales().get("en_US");
    }
}


