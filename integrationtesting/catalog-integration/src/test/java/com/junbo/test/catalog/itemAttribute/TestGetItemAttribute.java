/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemAttribute;

import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.ItemAttributeId;
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
 * For testing catalog get item attribute API
 */
public class TestGetItemAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItemAttribute.class);
    private ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
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
        Long invalidId = 0L;
        try {
            itemAttributeService.getItemAttribute(invalidId, 404);
            Assert.fail("Shouldn't get item attribute with wrong id");
        } catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get item attribute  with wrong id");
        }
    }

    @Property(
            priority = Priority.Dailies,
            features = "CatalogIntegration",
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

        //Prepare an item attribute
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute3 = itemAttributeService.postDefaultItemAttribute();

        listItemAttributeId.add(IdConverter.idLongToHexString(ItemAttributeId.class, itemAttribute1.getId()));
        paraMap.put("id", listItemAttributeId);
        Results<ItemAttribute> itemAttributes = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributes.getItems().size(), 1);
        Assert.assertTrue(isContain(itemAttributes, itemAttribute1));

        listItemAttributeId.add(IdConverter.idLongToHexString(ItemAttributeId.class, itemAttribute2.getId()));
        paraMap.put("id", listItemAttributeId);

        itemAttributes = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributes.getItems().size(), 2);
        Assert.assertTrue(isContain(itemAttributes, itemAttribute1));
        Assert.assertTrue(isContain(itemAttributes, itemAttribute2));

        listItemAttributeId.add(IdConverter.idLongToHexString(ItemAttributeId.class, itemAttribute3.getId()));
        paraMap.put("id", listItemAttributeId);

        itemAttributes = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributes.getItems().size(), 3);
        Assert.assertTrue(isContain(itemAttributes, itemAttribute1));
        Assert.assertTrue(isContain(itemAttributes, itemAttribute2));
        Assert.assertTrue(isContain(itemAttributes, itemAttribute3));

        listItemAttributeId.clear();
        listItemAttributeId.add("0000000000");
        listItemAttributeId.add(IdConverter.idLongToHexString(ItemAttributeId.class, itemAttribute2.getId()));
        listItemAttributeId.add(IdConverter.idLongToHexString(ItemAttributeId.class, itemAttribute3.getId()));

        itemAttributes = itemAttributeService.getItemAttributes(paraMap);
        Assert.assertEquals(itemAttributes.getItems().size(), 2);
        Assert.assertTrue(isContain(itemAttributes, itemAttribute2));
        Assert.assertTrue(isContain(itemAttributes, itemAttribute3));

    }

    private boolean isContain(Results<ItemAttribute> itemAttributeResults, ItemAttribute itemAttribute) {
        boolean contain = false;
        for (ItemAttribute itemAttribute1 : itemAttributeResults.getItems()){
            if (itemAttribute.getId().equals(itemAttribute1.getId())) {
                contain = true;
            }
        }
        return contain;
    }

}
