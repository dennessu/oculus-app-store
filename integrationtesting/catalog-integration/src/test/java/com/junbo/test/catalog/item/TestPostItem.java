/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.common.property.*;
import com.junbo.test.catalog.impl.*;
import com.junbo.test.catalog.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
  * @author Jason
  * Time: 4/1/2014
  * For testing catalog post item(s) API
 */
public class TestPostItem extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItem.class);
    private ItemService itemService = ItemServiceImpl.instance();
    private final String itemRequiredPara = "itemWithRequiredPara";
    private final String defaultItem = "defaultItem";
    private final String initRevValue = "1";
    private OrganizationId organizationId;

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/items",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Items only with required parameters and all parameters",
            steps = {
                    "1. Post test items only with required parameters",
                    "2. Verify the parameters",
                    "3. Post test item with optional parameters",
                    "4. Verify the parameters"
            }
    )
    @Test
    public void testPostItem() throws Exception {
        //Post test items only with required parameters
        Item testItemRequired = itemService.prepareItemEntity(itemRequiredPara, organizationId);
        Item itemRtn1 = itemService.postItem(testItemRequired);

        checkItemRequiredParams(itemRtn1, testItemRequired);

        //Post test item with optional params
        Item testItemFull = itemService.prepareItemEntity(defaultItem, organizationId);
        Item itemRtn2 = itemService.postItem(testItemFull);

        checkItemRequiredParams(itemRtn2, testItemFull);
        checkItemOptionalParams(itemRtn2, testItemFull);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Post v1/items",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post items with existed values(default offer, genres)",
            steps = {
                    "1. Prepare an item",
                    "2. Prepare an offer and genres for the item use",
                    "3. Post the item with the prepared offer and genres"
            }
    )
    @Test
    public void testPostItemWithExistedValues() throws Exception {

        OfferService offerService = OfferServiceImpl.instance();
        Offer offer = offerService.postDefaultOffer();

        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        List<String> genres = new ArrayList<>();
        genres.add(itemAttribute1.getId());
        genres.add(itemAttribute2.getId());

        Item testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setDefaultOffer(offer.getOfferId());
        testItem.setGenres(genres);

        Item itemPosted = itemService.postItem(testItem);
        Assert.assertEquals(itemPosted.getDefaultOffer(), offer.getOfferId());
        Assert.assertEquals(itemPosted.getGenres(), genres);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Post v1/items",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post items with invalid values",
            steps = {
                    "1. Prepare an item",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Try to post it and verify the expected error"
            }
    )
    @Test
    public void testPostItemInvalidScenarios() throws Exception {
        List<String> genresCategory = new ArrayList<>();
        List<String> genresInvalid = new ArrayList<>();
        genresInvalid.add("0L");
        genresInvalid.add("1L");

        //test ownerId is null
        Item testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setOwnerId(null);
        verifyExpectedError(testItem);

        //test currentRevision is not null
        testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setCurrentRevisionId("0L");
        verifyExpectedError(testItem);

        //test rev
        testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setRev(initRevValue);
        verifyExpectedError(testItem);

        //test type is invalid enums
        testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setType("invalid type");
        verifyExpectedError(testItem);

        //test defaultOffer is not existed
        testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setDefaultOffer("0L");
        verifyExpectedError(testItem);

        //test genres is not existed
        testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setGenres(genresInvalid);
        verifyExpectedError(testItem);

        //test genres type is category
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        genresCategory.add(offerAttribute.getId());

        testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setGenres(genresCategory);
        verifyExpectedError(testItem);

        //put all invalid scenarios together
        testItem = itemService.prepareItemEntity(defaultItem, organizationId);
        testItem.setOwnerId(null);
        testItem.setCurrentRevisionId("0L");
        testItem.setRev(initRevValue);
        testItem.setType("invalid type");
        testItem.setDefaultOffer("0L");
        testItem.setGenres(genresCategory);
        verifyExpectedError(testItem);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/items",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Disable,
            description = "Test Post developer item",
            steps = {
            }
    )
    @Test
    public void testPostDeveloperItem() throws Exception {
        ItemService itemService = ItemServiceImpl.instance();
        Item testItem = itemService.prepareItemEntity("developerItem");
        Item itemPosted = itemService.postItem(testItem);

        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
        ItemRevision itemRevision = itemRevisionService.prepareItemRevisionEntity("developerItemRevision");
        itemRevision.setItemId(itemPosted.getItemId());
        ItemRevision itemRevisionPosted = itemRevisionService.postItemRevision(itemRevision);

        //Approve the item revision
        itemRevisionPosted.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevisionPosted.getRevisionId(), itemRevisionPosted);
    }

    private void checkItemRequiredParams(Item itemActual, Item itemExpected) {
        Assert.assertEquals(itemActual.getType(), itemExpected.getType());
        Assert.assertEquals(itemActual.getOwnerId(), itemExpected.getOwnerId());
    }

    private void checkItemOptionalParams(Item itemActual, Item itemExpected) {
        Assert.assertEquals(itemActual.getAdminInfo(), itemExpected.getAdminInfo());
        Assert.assertEquals(itemActual.getFutureExpansion(), itemExpected.getFutureExpansion());
        Assert.assertEquals(itemActual.getGenres(), itemExpected.getGenres());
        Assert.assertEquals(itemActual.getDefaultOffer(), itemExpected.getDefaultOffer());
        Assert.assertEquals(itemActual.getCurrentRevisionId(), itemExpected.getCurrentRevisionId());
        Assert.assertEquals(itemActual.getRevisions(), itemExpected.getRevisions());
    }

    private void verifyExpectedError(Item item) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            itemService.postItem(item, 400);
            Assert.fail("Post item should fail");
        }
        catch (Exception ex) {
        }
    }

}
