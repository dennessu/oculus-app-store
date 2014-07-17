/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemAttribute;

import com.junbo.test.catalog.enums.CatalogItemAttributeType;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.ItemAttributeId;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 5/20/2014
 * For testing catalog get item attribute API
 */
public class TestGetItemAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItemAttribute.class);
    private ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOG);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/item-attributes/{itemAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an Item attribute by itsId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an item attribute",
                    "2. Get it by Id",
                    "3. Verify not able to get the item attribute by wrong Id"
            }
    )
    @Test
    public void testGetAnItemAttributeById() throws Exception {

        //Prepare an item attribute
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();
        //get the item by Id, assert not null
        ItemAttribute itemAttributeRtn = itemAttributeService.getItemAttribute(itemAttribute.getId());
        Assert.assertNotNull(itemAttributeRtn, "Can't get item attribute");

        //verify the invalid Id scenario
        String invalidId = "0L";
        try {
            itemAttributeService.getItemAttribute(invalidId, 404);
            Assert.fail("Shouldn't get item attribute with wrong id");
        } catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get item attribute  with wrong id");
        }
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/item-attributes?id=&id=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item attribute(s) by Id(s)(valid, invalid scenarios)",
            steps = {
                    "1. Prepare 3 item attributes",
                    "2. Get the item attributes by their ids"
            }
    )
    @Test
    public void testGetItemAttributesByIds() throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listItemAttributeId = new ArrayList<>();

        //Prepare some item attributes
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute3 = itemAttributeService.postDefaultItemAttribute();

        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute1.getId()));
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 1, itemAttribute1);

        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute2.getId()));
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 2, itemAttribute1, itemAttribute2);

        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute3.getId()));
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 3, itemAttribute1, itemAttribute2, itemAttribute3);

        listItemAttributeId.clear();
        listItemAttributeId.add("0000000000");
        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute2.getId()));
        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute3.getId()));
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 2, itemAttribute2, itemAttribute3);

        listItemAttributeId.clear();
        listItemAttributeId.add("0000000000");
        listItemAttributeId.add("0000000001");
        listItemAttributeId.add("0000000001");
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 0);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/item-attributes?id=&type=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item attribute(s) by Id(s) and type (valid, invalid scenarios)",
            steps = {
                    "1. Prepare 3 item attributes",
                    "2. Get the item attributes by their id and type"
            }
    )
    @Test
    public void testGetItemAttributesByIdType() throws Exception {

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listItemAttributeId = new ArrayList<>();
        List<String> listType = new ArrayList<>();

        //Prepare some item attributes
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute3 = itemAttributeService.postDefaultItemAttribute();

        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute1.getId()));
        listType.add(CatalogItemAttributeType.GENRE.getType());
        paraMap.put("attributeId", listItemAttributeId);
        paraMap.put("type", listType);
        verifyGetItemAttributes(paraMap, 1, itemAttribute1);

        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute2.getId()));
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 2, itemAttribute1, itemAttribute2);

        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute3.getId()));
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 3, itemAttribute1, itemAttribute2, itemAttribute3);

        listType.clear();
        listType.add("invalidType");
        paraMap.put("type", listType);
        verifyGetItemAttributes(paraMap, 0);

        listItemAttributeId.clear();
        listItemAttributeId.add("0000000000");
        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute2.getId()));
        listItemAttributeId.add(IdConverter.idToUrlString(ItemAttributeId.class, itemAttribute3.getId()));
        listType.clear();
        listType.add(CatalogItemAttributeType.GENRE.getType());
        paraMap.put("attributeId", listItemAttributeId);
        paraMap.put("type", listType);
        verifyGetItemAttributes(paraMap, 2, itemAttribute2, itemAttribute3);

        listItemAttributeId.clear();
        listItemAttributeId.add("0000000000");
        listItemAttributeId.add("0000000001");
        listItemAttributeId.add("0000000001");
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 0);
    }

    private void verifyGetItemAttributes(HashMap<String, List<String>> paraMap, int expectedRtnSize, ItemAttribute... ItemAttributes)
            throws Exception {
        Results<ItemAttribute> itemAttributesGet = itemAttributeService.getItemAttributes(paraMap);

        Assert.assertEquals(itemAttributesGet.getItems().size(), expectedRtnSize);
        for (ItemAttribute attribute : ItemAttributes) {
            Assert.assertTrue(isContain(itemAttributesGet, attribute));
        }
    }

}
