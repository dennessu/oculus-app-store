/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemAttribute;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.test.catalog.enums.CatalogItemAttributeType;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.HashMap;

/**
 * @author Jason
 * Time: 5/20/2014
 * For testing catalog post item attribute API
 */
public class TestPostItemAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostItemAttribute.class);
    private ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOG);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/item-attributes",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Item attribute",
            steps = {
                    "1. Post item attribute",
                    "2. Verify the return values as expected"
            }
    )
    @Test
    public void testPostItemAttribute() throws Exception {
        ItemAttribute itemAttribute = new ItemAttribute();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        itemAttribute.setType(CatalogItemAttributeType.GENRE.getType());

        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testItemAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        itemAttribute.setLocales(locales);

        ItemAttribute itemAttributePosted = itemAttributeService.postItemAttribute(itemAttribute);
        Assert.assertEquals(itemAttributePosted.getType(), CatalogItemAttributeType.GENRE.getType());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/item-attributes",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post item attribute with invalid values",
            steps = {
                    "1. Post item attribute with invalid values(like null, not null and some invalid enum values)",
                    "2. verify the expected error"
            }
    )
    @Test
    public void testPostItemAttributeInvalidScenarios() throws Exception {

        ItemAttribute itemAttribute = new ItemAttribute();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        //test everything is null
        verifyExpectedError(itemAttribute);

        //test invalid type
        itemAttribute.setType("invalid type");
        verifyExpectedError(itemAttribute);

        //test locales is null
        itemAttribute.setType(CatalogItemAttributeType.GENRE.getType());
        verifyExpectedError(itemAttribute);

        //test the attribute name is null
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        itemAttribute.setLocales(locales);
        verifyExpectedError(itemAttribute);

        //test the resource Age is not null
        attributeProperties.setName("testItemAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        locales.put("en_US", attributeProperties);
        itemAttribute.setLocales(locales);
        itemAttribute.setRev("1");
        verifyExpectedError(itemAttribute);
    }

    private void verifyExpectedError(ItemAttribute itemAttribute) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            itemAttributeService.postItemAttribute(itemAttribute, 400);
            Assert.fail("Post item attribute should fail");
        }
        catch (Exception ex) {
        }
    }

}
