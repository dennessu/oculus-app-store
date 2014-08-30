/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.common.id.ItemId;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.browse.document.SectionInfo;
import com.junbo.store.spec.model.browse.document.SectionInfoNode;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.purchase.MakeFreePurchaseResponse;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

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
        MakeFreePurchaseResponse freePurchaseResponse = testDataProvider.makeFreePurchase(offerId);

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
        testDataProvider.makeFreePurchase(offerId);

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
        SectionInfoNode sectionInfo = response.getSections().get(0);
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
        Assert.assertEquals(category, sectionLayoutResponse.getNext().getCategory());
        Assert.assertEquals(criteria, sectionLayoutResponse.getNext().getCriteria());

        // get rest of the items by get list
        listAllItems(category, criteria, pageSize, sectionLayoutResponse.getNext().getCursor());

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
}
