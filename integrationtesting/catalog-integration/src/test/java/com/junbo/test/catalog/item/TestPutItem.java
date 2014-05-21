/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;
import com.junbo.common.id.UserId;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason
 * Time: 4/10/2014
 * For testing catalog put item(s) API
 */
public class TestPutItem {

    private LogHelper logger = new LogHelper(TestGetItem.class);
    private ItemService itemService = ItemServiceImpl.instance();
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
            description = "Test put item successfully",
            steps = {
                    "1. Prepare a default item",
                    "2. Put the item with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutItem() throws Exception {
        //Prepare an item
        Item item = itemService.postDefaultItem(CatalogItemType.getRandom());

        //put item
        OfferService offerService = OfferServiceImpl.instance();
        Offer offer = offerService.postDefaultOffer();

        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        List<Long> genres = new ArrayList<>();
        genres.add(itemAttribute1.getId());
        genres.add(itemAttribute2.getId());

        Long userId = IdConverter.hexStringToId(UserId.class, developerId);

        item.setGenres(genres);
        item.setDefaultOffer(offer.getOfferId());
        item.setOwnerId(userId);

        Item itemPut = itemService.updateItem(item.getItemId(), item);

        //Verification
        Assert.assertEquals(itemPut.getGenres(), genres);
        Assert.assertEquals(itemPut.getDefaultOffer(), offer.getOfferId());
        Assert.assertEquals(itemPut.getOwnerId(), userId);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "CatalogIntegration",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put item successfully",
            steps = {
                    "1. Prepare a default item",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutItemInvalidScenarios() throws Exception {
        List<Long> genresCategory = new ArrayList<>();
        List<Long> genresInvalid = new ArrayList<>();
        genresInvalid.add(0L);
        genresInvalid.add(1L);

        //Prepare an item
        Item item = itemService.postDefaultItem(CatalogItemType.getRandom());
        Item itemForPut = item;
        Long itemId = item.getItemId();

        //update itself id
        item.setItemId(1L);
        verifyExpectedError(itemId, item);

        //test rev
        item.setResourceAge(0);
        verifyExpectedError(itemId, item);

        //test ownerId is null
        item.setOwnerId(null);
        verifyExpectedError(itemId, item);

        //can't update current revision id
        item.setCurrentRevisionId(0L);
        verifyExpectedError(itemId, item);

        //test type is invalid enums
        item.setType("invalid type");
        verifyExpectedError(itemId, item);

        //test defaultOffer is not existed
        item.setDefaultOffer(0L);
        verifyExpectedError(itemId, item);

        //test genres is not existed
        item.setGenres(genresInvalid);
        verifyExpectedError(itemId, item);

        //test genres type is category
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        genresCategory.add(offerAttribute.getId());

        item.setGenres(genresCategory);
        verifyExpectedError(itemId, item);

        //todo: type should not be updated
        if (item.getType().equalsIgnoreCase(CatalogItemType.DIGITAL.getItemType()) ) {
            item.setType(CatalogItemType.PHYSICAL.getItemType());
            verifyExpectedError(itemId, item);
        }
        else {
            item.setType(CatalogItemType.DIGITAL.getItemType());
            verifyExpectedError(itemId, item);
        }

    }

    private void verifyExpectedError(Long itemId, Item item) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            itemService.updateItem(itemId, item, 400);
            Assert.fail("Put item should fail");
        }
        catch (Exception ex) {
        }
    }

}
