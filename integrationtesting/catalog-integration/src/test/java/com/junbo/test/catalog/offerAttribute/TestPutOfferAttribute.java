/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerAttribute;

import com.junbo.catalog.spec.model.common.SimpleLocaleProperties;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.common.libs.LogHelper;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.HashMap;

/**
 * @author Jason
 * Time: 4/15/2014
 * For testing catalog put offer attribute(s) API
 */
public class TestPutOfferAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPutOfferAttribute.class);
    private OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
    private final String defaultLocale = "en_US";

    @Property(
            priority = Priority.Dailies,
            features = "Put v1/offer-attributes/{offerAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put offer attribute successfully",
            steps = {
                    "1. Prepare a default offer attribute",
                    "2. Put the offer attribute with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutOfferAttribute() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();

        //Prepare an offer attribute
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();

        //put the offer attribute
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        String newName = "testOfferAttribute_" + RandomFactory.getRandomStringOfAlphabet(10);
        String newDescription = RandomFactory.getRandomStringOfAlphabetOrNumeric(30);
        attributeProperties.setName(newName);
        attributeProperties.setDescription(newDescription);
        locales.put(defaultLocale, attributeProperties);
        offerAttribute.setLocales(locales);

        OfferAttribute offerAttributePut = offerAttributeService.updateOfferAttribute(offerAttribute.getId(), offerAttribute);

        //Verification
        Assert.assertTrue(offerAttributePut.getLocales().get(defaultLocale).getName().equalsIgnoreCase(newName));
        Assert.assertTrue(offerAttributePut.getLocales().get(defaultLocale).getDescription().equalsIgnoreCase(newDescription));
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Put v1/offer-attributes/{offerAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put offer attribute invalid scenarios",
            steps = {
                    "1. Prepare a default offer attribute",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutOfferAttributeInvalidScenarios() throws Exception {
        prepareCatalogAdminToken();

        //Prepare an offer attribute
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        String attributeId = offerAttribute.getId();

        //update itself id
        offerAttribute.setId("1L");
        verifyExpectedError(attributeId, offerAttribute);

        //test rev
        offerAttribute.setRev("0");
        verifyExpectedError(attributeId, offerAttribute);

        //test type is invalid enums
        offerAttribute.setType("invalid type");
        verifyExpectedError(attributeId, offerAttribute);

        //test 'set name to null'
        HashMap<String, SimpleLocaleProperties> locales = new HashMap<>();
        SimpleLocaleProperties attributeProperties = new SimpleLocaleProperties();
        attributeProperties.setName(null);
        attributeProperties.setDescription(RandomFactory.getRandomStringOfAlphabetOrNumeric(30));
        locales.put(defaultLocale, attributeProperties);
        offerAttribute.setLocales(locales);

        verifyExpectedError(attributeId, offerAttribute);
    }

    private void verifyExpectedError(String attributeId, OfferAttribute offerAttribute) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            offerAttributeService.updateOfferAttribute(attributeId, offerAttribute, 400);
            Assert.fail("Put offer attribute should fail");
        }
        catch (Exception ex) {
            logger.logInfo("expected exception" + ex);
        }
    }

}
