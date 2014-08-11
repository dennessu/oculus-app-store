/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerAttribute;

import com.junbo.test.catalog.enums.CatalogOfferAttributeType;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;
import com.junbo.common.model.Results;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 5/20/2014
 * For testing catalog get offer attribute API
 */
public class TestGetOfferAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetOfferAttribute.class);
    private OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/offer-attributes/{offerAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get an Offer attribute by itsId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare an offer attribute",
                    "2. Get it by Id",
                    "3. Verify not able to get the offer attribute by wrong Id"
            }
    )
    @Test
    public void testGetAnOfferAttributeById() throws Exception {
        prepareCatalogAdminToken();

        //Prepare an offer attribute
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        //get the offer by Id, assert not null
        OfferAttribute offerAttributeRtn = offerAttributeService.getOfferAttribute(offerAttribute.getId());
        Assert.assertNotNull(offerAttributeRtn, "Can't get offer attribute");

        //verify the invalid Id scenario
        String invalidId = "0L";
        try {
            offerAttributeService.getOfferAttribute(invalidId, 404);
            Assert.fail("Shouldn't get offer attribute with wrong id");
        } catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get offer attribute  with wrong id");
        }
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/offer-attributes?id=&id=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer attribute(s) by Id(s)(valid, invalid scenarios)",
            steps = {
                    "1. Prepare 3 offer attributes",
                    "2. Get the offer attributes by their ids"
            }
    )
    @Test
    public void testGetOfferAttributesByIds() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listOfferAttributeId = new ArrayList<>();

        //Prepare some offer attributes
        OfferAttribute offerAttribute1 = offerAttributeService.postDefaultOfferAttribute();
        OfferAttribute offerAttribute2 = offerAttributeService.postDefaultOfferAttribute();
        OfferAttribute offerAttribute3 = offerAttributeService.postDefaultOfferAttribute();

        listOfferAttributeId.add(offerAttribute1.getId());
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 1, offerAttribute1);

        listOfferAttributeId.add(offerAttribute2.getId());
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 2, offerAttribute1, offerAttribute2);

        listOfferAttributeId.add(offerAttribute3.getId());
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 3, offerAttribute1, offerAttribute2, offerAttribute3);

        listOfferAttributeId.clear();
        listOfferAttributeId.add("0000000000");
        listOfferAttributeId.add(offerAttribute2.getId());
        listOfferAttributeId.add(offerAttribute3.getId());
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 2, offerAttribute2, offerAttribute3);

        listOfferAttributeId.clear();
        listOfferAttributeId.add("0000000000");
        listOfferAttributeId.add("0000000001");
        listOfferAttributeId.add("0000000001");
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 0);
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/offer-attributes?id=&type=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offer attribute(s) by Id(s)(valid, invalid scenarios)",
            steps = {
                    "1. Prepare 3 offer attributes",
                    "2. Get the offer attributes by their id and type"
            }
    )
    @Test
    public void testGetOfferAttributesByIdType() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listOfferAttributeId = new ArrayList<>();
        List<String> listType = new ArrayList<>();

        //Prepare some offer attributes
        OfferAttribute offerAttribute1 = offerAttributeService.postDefaultOfferAttribute();
        OfferAttribute offerAttribute2 = offerAttributeService.postDefaultOfferAttribute();
        OfferAttribute offerAttribute3 = offerAttributeService.postDefaultOfferAttribute();

        listOfferAttributeId.add(offerAttribute1.getId());
        listType.add(CatalogOfferAttributeType.CATEGORY.getType());
        paraMap.put("attributeId", listOfferAttributeId);
        paraMap.put("type", listType);
        verifyGetOfferAttributes(paraMap, 1, offerAttribute1);

        listOfferAttributeId.add(offerAttribute2.getId());
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 2, offerAttribute1, offerAttribute2);

        listOfferAttributeId.add(offerAttribute3.getId());
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 3, offerAttribute1, offerAttribute2, offerAttribute3);

        listType.clear();
        listType.add("invalidType");
        paraMap.put("type", listType);
        verifyGetOfferAttributes(paraMap, 0);

        listOfferAttributeId.clear();
        listOfferAttributeId.add("0000000000");
        listOfferAttributeId.add(offerAttribute2.getId());
        listOfferAttributeId.add(offerAttribute3.getId());
        listType.clear();
        listType.add(CatalogOfferAttributeType.CATEGORY.getType());
        paraMap.put("attributeId", listOfferAttributeId);
        paraMap.put("type", listType);
        verifyGetOfferAttributes(paraMap, 2, offerAttribute2, offerAttribute3);

        listOfferAttributeId.clear();
        listOfferAttributeId.add("0000000000");
        listOfferAttributeId.add("0000000001");
        listOfferAttributeId.add("0000000001");
        paraMap.put("attributeId", listOfferAttributeId);
        verifyGetOfferAttributes(paraMap, 0);
    }

    private void verifyGetOfferAttributes(HashMap<String, List<String>> paraMap, int expectedRtnSize, OfferAttribute... OfferAttributes)
            throws Exception {
        Results<OfferAttribute> offerAttributesGet = offerAttributeService.getOfferAttributes(paraMap);

        Assert.assertEquals(offerAttributesGet.getItems().size(), expectedRtnSize);
        for (OfferAttribute attribute : OfferAttributes) {
            Assert.assertTrue(isContain(offerAttributesGet, attribute));
        }
    }

}
