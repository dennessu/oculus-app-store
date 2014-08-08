/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemAttribute;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.HashMap;

/**
 * @author Jason
 * Time: 4/15/2014
 * For testing catalog put item attribute(s) API
 */
public class TestPutItemAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPutItemAttribute.class);
    private ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
    private final String defaultLocale = "en_US";

    @Property(
            priority = Priority.Dailies,
            features = "Put v1/item-attributes/{itemAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put item attribute successfully",
            steps = {
                    "1. Prepare a default item attribute",
                    "2. Put the item attribute with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutItemAttribute() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        //Prepare an item attribute
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();

        //put the item attribute
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        String newName = "testItemAttribute_" + RandomFactory.getRandomStringOfAlphabet(10);
        String newDescription = RandomFactory.getRandomStringOfAlphabetOrNumeric(30);
        attributeProperties.setName(newName);
        attributeProperties.setDescription(newDescription);
        locales.put(defaultLocale, attributeProperties);
        itemAttribute.setLocales(locales);

        ItemAttribute itemAttributePut = itemAttributeService.updateItemAttribute(itemAttribute.getId(), itemAttribute);

        //Verification
        Assert.assertTrue(itemAttributePut.getLocales().get(defaultLocale).getName().equalsIgnoreCase(newName));
        Assert.assertTrue(itemAttributePut.getLocales().get(defaultLocale).getDescription().equalsIgnoreCase(newDescription));
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Put v1/item-attributes/{itemAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put item attribute invalid scenarios",
            steps = {
                    "1. Prepare a default item attribute",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutItemAttributeInvalidScenarios() throws Exception {
        prepareCatalogAdminToken();

        //Prepare an item attribute
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();
        String attributeId = itemAttribute.getId();

        //update itself id
        itemAttribute.setId("1L");
        verifyExpectedError(attributeId, itemAttribute);

        //test rev
        itemAttribute.setRev("0");
        verifyExpectedError(attributeId, itemAttribute);

        //test type is invalid enums
        itemAttribute.setType("invalid type");
        verifyExpectedError(attributeId, itemAttribute);

        //test 'set name to null'
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName(null);
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put(defaultLocale, attributeProperties);
        itemAttribute.setLocales(locales);

        verifyExpectedError(attributeId, itemAttribute);
    }

    private void verifyExpectedError(String attributeId, ItemAttribute itemAttribute) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            itemAttributeService.updateItemAttribute(attributeId, itemAttribute, 400);
            Assert.fail("Put item should fail");
        }
        catch (Exception ex) {
            logger.logInfo("expected exception" + ex);
        }
    }

}
