/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerAttribute;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.test.catalog.enums.CatalogOfferAttributeType;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.test.catalog.OfferAttributeService;
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
 * For testing catalog post offer attribute API
 */
public class TestPostOfferAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostOfferAttribute.class);
    private OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOG);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/offer-attributes",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Offer attribute",
            steps = {
                    "1. Post offer attribute",
                    "2. Verify the return values as expected"
            }
    )
    @Test
    public void testPostOfferAttribute() throws Exception {
        OfferAttribute offerAttribute = new OfferAttribute();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        offerAttribute.setType(CatalogOfferAttributeType.CATEGORY.getType());

        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName("testOfferAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        offerAttribute.setLocales(locales);

        OfferAttribute offerAttributePosted = offerAttributeService.postOfferAttribute(offerAttribute);
        Assert.assertEquals(offerAttributePosted.getType(), CatalogOfferAttributeType.CATEGORY.getType());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/offer-attributes",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post offer attribute with invalid values",
            steps = {
                    "1. Post offer attribute with invalid values(like null, not null and some invalid enum values)",
                    "2. verify the expected error"
            }
    )
    @Test
    public void testPostOfferAttributeInvalidScenarios() throws Exception {

        OfferAttribute offerAttribute = new OfferAttribute();
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        //test everything is null
        verifyExpectedError(offerAttribute);

        //test invalid type
        offerAttribute.setType("invalid type");
        verifyExpectedError(offerAttribute);

        //test locales is null
        offerAttribute.setType(CatalogOfferAttributeType.CATEGORY.getType());
        verifyExpectedError(offerAttribute);

        //test the attribute name is null
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put("en_US", attributeProperties);
        offerAttribute.setLocales(locales);
        verifyExpectedError(offerAttribute);

        //test the resource Age is not null
        attributeProperties.setName("testOfferAttribute_" + RandomFactory.getRandomStringOfAlphabet(10));
        locales.put("en_US", attributeProperties);
        offerAttribute.setLocales(locales);
        offerAttribute.setRev("1");
        verifyExpectedError(offerAttribute);
    }

    private void verifyExpectedError(OfferAttribute offerAttribute) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            offerAttributeService.postOfferAttribute(offerAttribute, 400);
            Assert.fail("Post offer attribute should fail");
        }
        catch (Exception ex) {
        }
    }

}
