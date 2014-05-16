/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.common.model.Link;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.lang.reflect.Field;
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
        UserService userService = UserServiceImpl.instance();
        String developerId = userService.PostUser();

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
                    "2. Set the required parameters to null or other invalid values",
                    "3. Try to post it and verify the expected error"
            }
    )
    @Test
    public void testPostItemInvalidScenarios() throws Exception {
        UserService userService = UserServiceImpl.instance();
        String developerId = userService.PostUser();
        String[] fieldsCantBeNull = new String[]{"ownerId", "type"};
        String[] fieldsMustBeNull = new String[]{"currentRevision", "entitlementDefId"};
        Map<String , Object> invalidValues = new HashMap<>();
        List<Long> genres = new ArrayList<>();
        Link revisions = new Link();
        revisions.setId("0");
        revisions.setHref("invalidHref");
        genres.add(0L);
        genres.add(1L);
        invalidValues.put("type", "invalidType");
        invalidValues.put("revisions", revisions);
        invalidValues.put("defaultOffer", 0L);
        invalidValues.put("genres", genres);

        Item testItem;

        for (int i = 0; i < fieldsCantBeNull.length; i ++) {
            case
            testItem = itemService.prepareItemEntity(defaultItem, developerId);
            itemField.set(testItem, null);
            verifyExpectedError(testItem);
        }

        for (Field itemField : itemFields) {
            if (Arrays.asList(fieldsCantBeNull).contains(itemField.getName())) {
                testItem = itemService.prepareItemEntity(defaultItem, developerId);
                itemField.set(testItem, null);
                verifyExpectedError(testItem);
            }
            if (Arrays.asList(fieldsMustBeNull).contains(itemField.getName())) {
                testItem = itemService.prepareItemEntity(defaultItem, developerId);
                itemField.set(testItem, 0L);
                verifyExpectedError(testItem);
            }
            if (invalidValues.containsKey(itemField.getName())) {
                testItem = itemService.prepareItemEntity(defaultItem, developerId);
                itemField.set(testItem, invalidValues.get(itemField.getName()));
            }
        }

        //verify rev
        testItem = itemService.prepareItemEntity(defaultItem, developerId);
        testItem.setRev(initRevValue);
        verifyExpectedError(testItem);
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
            //Error code 400 means "Missing Input field"
            itemService.postItem(item, 400);
            Assert.fail("Post item should fail");
        }
        catch (Exception ex) {
        }
    }
}
