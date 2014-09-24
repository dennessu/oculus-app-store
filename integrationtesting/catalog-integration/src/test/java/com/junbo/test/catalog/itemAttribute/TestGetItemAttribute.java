/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemAttribute;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.test.catalog.enums.CatalogItemAttributeType;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.enums.LocaleAccuracy;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 5/20/2014
 * For testing catalog get item attribute API
 */
public class TestGetItemAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItemAttribute.class);
    private ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();

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
        prepareCatalogAdminToken();

        //Prepare an item attribute
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();
        //get the item by Id, assert not null
        ItemAttribute itemAttributeRtn = itemAttributeService.getItemAttribute(itemAttribute.getId());
        Assert.assertNotNull(itemAttributeRtn, "Can't get item attribute");

        //verify the invalid Id scenario
        String invalidId = "0L";
        try {
            itemAttributeService.getItemAttribute(invalidId, null, 404);
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
        prepareCatalogAdminToken();

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listItemAttributeId = new ArrayList<>();

        //Prepare some item attributes
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute3 = itemAttributeService.postDefaultItemAttribute();

        listItemAttributeId.add(itemAttribute1.getId());
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 1, itemAttribute1);

        listItemAttributeId.add(itemAttribute2.getId());
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 2, itemAttribute1, itemAttribute2);

        listItemAttributeId.add(itemAttribute3.getId());
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 3, itemAttribute1, itemAttribute2, itemAttribute3);

        listItemAttributeId.clear();
        listItemAttributeId.add("0000000000");
        listItemAttributeId.add(itemAttribute2.getId());
        listItemAttributeId.add(itemAttribute3.getId());
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
        prepareCatalogAdminToken();

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listItemAttributeId = new ArrayList<>();
        List<String> listType = new ArrayList<>();

        //Prepare some item attributes
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute3 = itemAttributeService.postDefaultItemAttribute();

        listItemAttributeId.add(itemAttribute1.getId());
        listType.add(CatalogItemAttributeType.GENRE.getType());
        paraMap.put("attributeId", listItemAttributeId);
        paraMap.put("type", listType);
        verifyGetItemAttributes(paraMap, 1, itemAttribute1);

        listItemAttributeId.add(itemAttribute2.getId());
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 2, itemAttribute1, itemAttribute2);

        listItemAttributeId.add(itemAttribute3.getId());
        paraMap.put("attributeId", listItemAttributeId);
        verifyGetItemAttributes(paraMap, 3, itemAttribute1, itemAttribute2, itemAttribute3);

        listType.clear();
        listType.add("invalidType");
        paraMap.put("type", listType);
        verifyGetItemAttributes(paraMap, 0);

        listItemAttributeId.clear();
        listItemAttributeId.add("0000000000");
        listItemAttributeId.add(itemAttribute2.getId());
        listItemAttributeId.add(itemAttribute3.getId());
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

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/item-attributes?locale=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get item attribute(s) with locale option",
            steps = {
                    "1. Prepare 3 item attributes",
                    "2. Get the item attributes with locale option"
            }
    )
    @Test
    public void testGetItemAttributeWithLocale() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listItemAttributeId = new ArrayList<>();
        List<String> listType = new ArrayList<>();
        List<String> listLocale = new ArrayList<>();

        //Prepare some item attributes
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute3 = itemAttributeService.postDefaultItemAttribute();

        ItemAttribute itemAttributeRtn1 = itemAttributeService.getItemAttribute(itemAttribute1.getId());
        Assert.assertEquals(itemAttributeRtn1.getLocaleAccuracy(), LocaleAccuracy.HIGH.name());

        Map<String, SimpleLocaleProperties> itemAttributeProperty = itemAttributeRtn1.getLocales();

        itemAttributeRtn1 = itemAttributeService.getItemAttribute(itemAttribute1.getId(), "en_US");
        Assert.assertEquals(itemAttributeRtn1.getLocaleAccuracy(), LocaleAccuracy.HIGH.name());

        itemAttributeRtn1 = itemAttributeService.getItemAttribute(itemAttribute1.getId(), "en_CA");
        Assert.assertEquals(itemAttributeRtn1.getLocaleAccuracy(), LocaleAccuracy.LOW.name());

        SimpleLocaleProperties properties = new SimpleLocaleProperties();
        properties.setName(RandomFactory.getRandomStringOfAlphabet(10));
        itemAttributeProperty.put("zh_CN", properties);
        itemAttributeRtn1.setLocales(itemAttributeProperty);

        itemAttributeService.updateItemAttribute(itemAttribute1.getId(), itemAttributeRtn1);
        itemAttributeRtn1 = itemAttributeService.getItemAttribute(itemAttribute1.getId(), "zh_CN");
        Assert.assertEquals(itemAttributeRtn1.getLocaleAccuracy(), LocaleAccuracy.MEDIUM.name());

        listItemAttributeId.add(itemAttribute1.getId());
        listItemAttributeId.add(itemAttribute2.getId());
        listItemAttributeId.add(itemAttribute3.getId());
        paraMap.put("attributeId", listItemAttributeId);

        Results<ItemAttribute> itemAttributeResults = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributeResults.getItems().size(), 3);
        Assert.assertEquals(itemAttributeResults.getItems().get(0).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(1).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(2).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());

        listLocale.add("en_US");
        paraMap.put("locale", listLocale);

        itemAttributeResults = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributeResults.getItems().size(), 3);
        Assert.assertEquals(itemAttributeResults.getItems().get(0).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(1).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(2).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());

        listLocale.clear();
        listLocale.add("zh_CN");
        paraMap.put("locale", listLocale);

        listItemAttributeId.clear();
        listItemAttributeId.add(itemAttribute1.getId());
        paraMap.put("attributeId", listItemAttributeId);

        itemAttributeResults = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributeResults.getItems().size(), 1);
        Assert.assertEquals(itemAttributeResults.getItems().get(0).getLocaleAccuracy(), LocaleAccuracy.MEDIUM.name());

        listItemAttributeId.clear();
        listItemAttributeId.add(itemAttribute2.getId());
        listItemAttributeId.add(itemAttribute3.getId());
        paraMap.put("attributeId", listItemAttributeId);

        itemAttributeResults = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributeResults.getItems().size(), 2);
        Assert.assertEquals(itemAttributeResults.getItems().get(0).getLocaleAccuracy(), LocaleAccuracy.LOW.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(1).getLocaleAccuracy(), LocaleAccuracy.LOW.name());

        listLocale.clear();
        listLocale.add("es_ES");
        paraMap.put("locale", listLocale);
        listItemAttributeId.add(itemAttribute1.getId());
        paraMap.put("attributeId", listItemAttributeId);

        itemAttributeResults = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributeResults.getItems().size(), 3);
        Assert.assertEquals(itemAttributeResults.getItems().get(0).getLocaleAccuracy(), LocaleAccuracy.LOW.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(1).getLocaleAccuracy(), LocaleAccuracy.LOW.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(2).getLocaleAccuracy(), LocaleAccuracy.LOW.name());

        listItemAttributeId.clear();
        listItemAttributeId.add(itemAttribute2.getId());
        listItemAttributeId.add(itemAttribute3.getId());
        paraMap.put("attributeId", listItemAttributeId);

        listType.add(CatalogItemAttributeType.GENRE.name());
        paraMap.put("type", listType);

        itemAttributeResults = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributeResults.getItems().size(), 2);
        Assert.assertEquals(itemAttributeResults.getItems().get(0).getLocaleAccuracy(), LocaleAccuracy.LOW.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(1).getLocaleAccuracy(), LocaleAccuracy.LOW.name());

        listLocale.clear();
        listLocale.add("en_US");
        paraMap.put("locale", listLocale);
        itemAttributeResults = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributeResults.getItems().size(), 2);
        Assert.assertEquals(itemAttributeResults.getItems().get(0).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());
        Assert.assertEquals(itemAttributeResults.getItems().get(1).getLocaleAccuracy(), LocaleAccuracy.HIGH.name());
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
