/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemRevision;

import com.junbo.catalog.spec.model.common.Price;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jason
 * Time: 7/4/2014
 * For testing catalog post item revision API
 */
public class TestPostItemRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostItemRevision.class);

    private Item item1;
    private Item item2;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";
    private final String fullItemRevisionFileName = "fullItemRevision";

    private ItemService itemService = ItemServiceImpl.instance();
    private ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();

        item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/item-revisions",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Post Item Revisions",
            steps = {
                    "1. Post test item revision only with required fields",
                    "2. Verify the returned values are the same with prepared",
                    "3. Post test item with optional fields",
                    "4. Verify the returned values are the same with prepared"
            }
    )
    @Test
    public void testPostItemRevision() throws Exception {

        //Post an item revision only with required fields
        ItemRevision itemRevisionPrepared = new ItemRevision();
        Map<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();

        itemRevisionPrepared.setItemId(item1.getItemId());
        itemRevisionPrepared.setOwnerId(organizationId);
        itemRevisionPrepared.setStatus(CatalogEntityStatus.DRAFT.name());

        itemRevisionLocaleProperties.setName("testItemRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        locales.put(defaultLocale, itemRevisionLocaleProperties);
        itemRevisionPrepared.setLocales(locales);

        ItemRevision itemRevisionRtn = itemRevisionService.postItemRevision(itemRevisionPrepared);

        checkItemRevisionRequiredFields(itemRevisionRtn, itemRevisionPrepared);

        //Post an item revision with optional fields
        ItemRevision testItemRevisionFull = itemRevisionService.prepareItemRevisionEntity(fullItemRevisionFileName);

        testItemRevisionFull.setItemId(item1.getItemId());
        testItemRevisionFull.setOwnerId(organizationId);
        List<String> hostItemIds = new ArrayList<>();
        hostItemIds.add(item2.getItemId());
        testItemRevisionFull.setIapHostItemIds(hostItemIds);

        ItemRevision testItemRevisionRtn = itemRevisionService.postItemRevision(testItemRevisionFull);

        checkItemRevisionRequiredFields(testItemRevisionRtn, testItemRevisionFull);
        checkItemRevisionOptionalFields(testItemRevisionRtn, testItemRevisionFull);
    }

    private void checkItemRevisionRequiredFields(ItemRevision itemRevisionActual, ItemRevision itemRevisionExpected) {
        Assert.assertEquals(itemRevisionActual.getItemId(), itemRevisionExpected.getItemId());
        Assert.assertEquals(itemRevisionActual.getOwnerId(), itemRevisionExpected.getOwnerId());

        ItemRevisionLocaleProperties localePropertiesActual = itemRevisionActual.getLocales().get(defaultLocale);
        ItemRevisionLocaleProperties localePropertiesExpected = itemRevisionExpected.getLocales().get(defaultLocale);

        Assert.assertEquals(localePropertiesActual.getName(), localePropertiesExpected.getName());
    }

    private void checkItemRevisionOptionalFields(ItemRevision itemRevisionActual, ItemRevision itemRevisionExpected) {
        Assert.assertEquals(itemRevisionActual.getStatus(), itemRevisionExpected.getStatus());
        Assert.assertEquals(itemRevisionActual.getRollupPackageName(), itemRevisionExpected.getRollupPackageName());
        Assert.assertEquals(itemRevisionActual.getPackageName(), itemRevisionExpected.getPackageName());
        Assert.assertEquals(itemRevisionActual.getSku(), itemRevisionExpected.getSku());

        //Compare msrp
        Price actual = itemRevisionActual.getMsrp();
        Price expected = itemRevisionExpected.getMsrp();
        Assert.assertTrue(actual.getPriceTier().equalsIgnoreCase(expected.getPriceTier()));
        Assert.assertTrue(actual.getPriceType().equalsIgnoreCase(expected.getPriceType()));
        Assert.assertTrue(actual.getPrices().equals(expected.getPrices()));


    }

}
