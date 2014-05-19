/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.*;

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
    private String developerId;

    @BeforeClass
    private void PrepareTestData() throws Exception {
        UserService userService = UserServiceImpl.instance();
        developerId = userService.PostUser();
    }

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
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
        Item testItemRequired = itemService.prepareItemEntity(itemRequiredPara, developerId);
        Item itemRtn1 = itemService.postItem(testItemRequired);

        checkItemRequiredParams(itemRtn1, testItemRequired);

        //Post test item with optional params
        Item testItemFull = itemService.prepareItemEntity(defaultItem, developerId);
        Item itemRtn2 = itemService.postItem(testItemFull);

        checkItemRequiredParams(itemRtn2, testItemFull);
        checkItemOptionalParams(itemRtn2, testItemFull);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "CatalogIntegration",
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
        UserService userService = UserServiceImpl.instance();
        String developerId = userService.PostUser();

        List<Long> genres = new ArrayList<>();
        genres.add(0L);
        genres.add(1L);

        //test ownerId is null
        Item testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setOwnerId(null);
        verifyExpectedError(testItem);

        //test currentRevision is not null
        testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setCurrentRevisionId(0L);
        verifyExpectedError(testItem);

        //test rev
        testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setRev(initRevValue);
        verifyExpectedError(testItem);

        //test type is invalid enums
        testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setType("invalid type");
        verifyExpectedError(testItem);

        //test defaultOffer is not existed
        testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setDefaultOffer(0L);
        verifyExpectedError(testItem);

        //test genres is not existed
        testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setGenres(genres);
        verifyExpectedError(testItem);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "CatalogIntegration",
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
        List<Long> genres = new ArrayList<>();
        genres.add(itemAttribute1.getId());
        genres.add(itemAttribute2.getId());

        Item testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setDefaultOffer(offer.getOfferId());
        testItem.setGenres(genres);

        Item itemPosted = itemService.postItem(testItem);
        Assert.assertEquals(itemPosted.getDefaultOffer(), offer.getOfferId());
        Assert.assertEquals(itemPosted.getGenres(), genres);
    }

    private void checkItemRequiredParams(Item itemActual, Item itemExpected) {
        Assert.assertEquals(itemActual.getType(), itemExpected.getType());
        Assert.assertEquals(itemActual.getOwnerId(), itemExpected.getOwnerId());
    }

    private void checkItemOptionalParams(Item itemActual, Item itemExpected) {
        Assert.assertEquals(itemActual.getRev(), initRevValue);
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
