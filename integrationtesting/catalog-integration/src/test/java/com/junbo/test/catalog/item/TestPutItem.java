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
            features = "Put v1/items/{itemId}",
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
            features = "Put v1/items/{itemId}",
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

        //update itself id
        Long itemId = item.getItemId();
        item.setItemId(1L);
        verifyExpectedError(itemId, item);

        //test rev
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        item.setResourceAge(0);
        verifyExpectedError(item.getItemId(), item);

        //test ownerId is null
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        item.setOwnerId(null);
        verifyExpectedError(item.getItemId(), item);

        //can't update current revision id
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        item.setCurrentRevisionId(0L);
        verifyExpectedError(item.getItemId(), item);

        //test type is invalid enums
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        item.setType("invalid type");
        verifyExpectedError(item.getItemId(), item);

        //test defaultOffer is not existed
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        item.setDefaultOffer(0L);
        verifyExpectedError(item.getItemId(), item);

        //test genres is not existed
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        item.setGenres(genresInvalid);
        verifyExpectedError(item.getItemId(), item);

        //test genres type is category
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        genresCategory.add(offerAttribute.getId());

        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        item.setGenres(genresCategory);
        verifyExpectedError(item.getItemId(), item);

        //todo: type should not be updated
        item = itemService.postDefaultItem(CatalogItemType.getRandom());
        if (item.getType().equalsIgnoreCase(CatalogItemType.DIGITAL.getItemType()) ) {
            item.setType(CatalogItemType.PHYSICAL.getItemType());
            verifyExpectedError(item.getItemId(), item);
        }
        else {
            item.setType(CatalogItemType.DIGITAL.getItemType());
            verifyExpectedError(item.getItemId(), item);
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
