/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.advancedContentSearch;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.catalog.spec.model.common.RevisionNotes;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.ItemAttributeService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jason
 * Time: 6/27/2014
 * For testing catalog item advanced search
 */
public class ItemSearch extends BaseTestClass {

    private LogHelper logger = new LogHelper(ItemSearch.class);

    private Item item1;
    private Item item2;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";

    private ItemService itemService = ItemServiceImpl.instance();
    private ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organizationId = organizationService.postDefaultOrganization().getId();

        item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        //put item to add genre
        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();

        List<String> genres = new ArrayList<>();
        genres.add(itemAttribute1.getId());
        item1.setGenres(genres);
        item1 = itemService.updateItem(item1.getItemId(), item1);

        //release the item2 with customized properties
        ItemRevision itemRevisionPrepared;

        if (item2.getType().equalsIgnoreCase(CatalogItemType.APP.getItemType()) ||
                item2.getType().equalsIgnoreCase(CatalogItemType.DOWNLOADED_ADDITION.getItemType())) {
            itemRevisionPrepared = itemRevisionService.prepareItemRevisionEntity(defaultDigitalItemRevisionFileName);
        } else if (item2.getType().equalsIgnoreCase(CatalogItemType.STORED_VALUE.getItemType())) {
            itemRevisionPrepared = itemRevisionService.prepareItemRevisionEntity(defaultStoredValueItemRevisionFileName);
        } else {
            itemRevisionPrepared = itemRevisionService.prepareItemRevisionEntity(defaultPhysicalItemRevisionFileName);
        }

        itemRevisionPrepared.setItemId(item2.getItemId());

        List<String> iapHostItemId = new ArrayList<>();
        iapHostItemId.add(item1.getItemId());
        itemRevisionPrepared.setIapHostItemIds(iapHostItemId);

        //set sku
        itemRevisionPrepared.setSku(RandomFactory.getRandomStringOfAlphabet(5));

        //set packageName
        itemRevisionPrepared.setPackageName(RandomFactory.getRandomStringOfAlphabet(10));

        //set userInteractionMode
        List<String> userMode = new ArrayList<>();
        userMode.add("SINGLE_USER");
        userMode.add("MULTI_USER");
        userMode.add("CO_OP");
        itemRevisionPrepared.setUserInteractionModes(userMode);

        //set platform
        List<String> platform = new ArrayList<>();
        platform.add("PC");
        platform.add("MAC");
        platform.add("LINUX");
        platform.add("ANDROID");
        itemRevisionPrepared.setPlatforms(platform);

        //set name, revisionNotes, long description and short description
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();
        RevisionNotes releaseNotes = new RevisionNotes();
        releaseNotes.setShortNotes("shortReleaseNotes_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        releaseNotes.setLongNotes("longReleaseNotes_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        itemRevisionLocaleProperties.setName("testItemRevision_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        itemRevisionLocaleProperties.setReleaseNotes(releaseNotes);
        itemRevisionLocaleProperties.setLongDescription("longDescription_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        itemRevisionLocaleProperties.setShortDescription("shortDescription_" + RandomFactory.getRandomStringOfAlphabetOrNumeric(10));
        HashMap<String, ItemRevisionLocaleProperties> locales = new HashMap<>();
        locales.put("default", itemRevisionLocaleProperties);
        locales.put(defaultLocale, itemRevisionLocaleProperties);
        itemRevisionPrepared.setLocales(locales);

        ItemRevision itemRevisionPost = itemRevisionService.postItemRevision(itemRevisionPrepared);
        itemRevisionPost.setStatus(CatalogEntityStatus.APPROVED.getEntityStatus());
        itemRevisionService.updateItemRevision(itemRevisionPost.getRevisionId(), itemRevisionPost);

        item2 = itemService.getItem(item2.getItemId());
    }

    @Property(
            priority = Priority.BVT,
            features = "Get v1/items?q=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get items by advanced query",
            steps = {
                    "1. Prepare some items",
                    "2. Get the items by advanced query",
                    "3. Release the items",
                    "4. Get the items by advanced query again"
            }
    )
    @Test
    public void testGetItemsByOnlyOneOption() throws Exception {

        String itemId1 = item1.getItemId();
        String itemId2 = item2.getItemId();

        ItemRevision itemRevision = itemRevisionService.getItemRevision(item2.getCurrentRevisionId());
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = itemRevision.getLocales().get(defaultLocale);

        //itemId
        buildSearchQuery("itemId:" + itemId1, 1, itemId1);

        //default -- itemId
        buildSearchQuery(itemId1, 2, itemId1);

        //ownerId
        buildSearchQuery("ownerId:'" + organizationId.toString() + "'", 2, itemId1, itemId2);

        //default -- ownerId
        buildSearchQuery("'" + organizationId.toString() + "'", 2, itemId1, itemId2);

        //genreId
        buildSearchQuery("genreId:" + item1.getGenres().get(0), 1, itemId1);

        //default - genreId
        buildSearchQuery(item1.getGenres().get(0), 1, itemId1);

        //release the item1
        item1 = releaseItem(item1);

        //revisionId
        buildSearchQuery("revisionId:" + item1.getCurrentRevisionId(), 1, itemId1);
        buildSearchQuery("revisionId:" + item2.getCurrentRevisionId(), 1, itemId2);

        //default -- revisionId
        buildSearchQuery(item1.getCurrentRevisionId(), 1, itemId1);

        //sku
        String sku = itemRevision.getSku();
        buildSearchQuery("sku:" + sku, 1, itemId2);

        //default -- sku
        buildSearchQuery(sku, 1, itemId2);

        //packageName
        String packageName = itemRevision.getPackageName();
        buildSearchQuery("packageName:" + packageName, 1, itemId2);

        //default -- packageName
        buildSearchQuery(packageName, 1, itemId2);

        //hostItemId
        List<String> hostItemId = itemRevision.getIapHostItemIds();
        if (!hostItemId.isEmpty()) {
            buildSearchQuery("hostItemId:" + hostItemId.get(0), 1, itemId2);
        }

        //default -- hostItemId
        if (!hostItemId.isEmpty()) {
            buildSearchQuery(hostItemId.get(0), 2, itemId2);
        }

        //name
        String name = itemRevisionLocaleProperties.getName();
        buildSearchQuery("name:" + name, 1, itemId2);

        //default -- name
        buildSearchQuery(name, 1, itemId2);

        //longRevisionNotes
        String longRevisionNotes = itemRevisionLocaleProperties.getReleaseNotes().getLongNotes();
        buildSearchQuery("longNotes:" + longRevisionNotes, 1, itemId2);

        //default -- revisionNotes
        buildSearchQuery(longRevisionNotes, 1, itemId2);

        //shortRevisionNotes
        String shortRevisionNotes = itemRevisionLocaleProperties.getReleaseNotes().getShortNotes();
        buildSearchQuery("shortNotes:" + shortRevisionNotes, 1, itemId2);

        //default -- revisionNotes
        buildSearchQuery(shortRevisionNotes, 1, itemId2);

        //longDescription
        String longDescription = itemRevisionLocaleProperties.getLongDescription();
        buildSearchQuery("longDescription:" + longDescription, 1, itemId2);

        //default -- longDescription
        buildSearchQuery(longDescription, 1, itemId2);

        //shortDescription
        String shortDescription = itemRevisionLocaleProperties.getShortDescription();
        buildSearchQuery("shortDescription:" + shortDescription, 1, itemId2);

        //default -- shortDescription
        buildSearchQuery(shortDescription, 1, itemId2);

    }

    @Property(
            priority = Priority.BVT,
            features = "Get v1/items?q= AND/OR",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get items by advanced query",
            steps = {
                    "1. Prepare some items",
                    "2. Get the items by advanced query",
                    "3. Release the items",
                    "4. Get the items by advanced query again"
            }
    )
    @Test
    public void testGetItemsByCombinedOption() throws Exception {
        String itemId1 = item1.getItemId();
        String itemType1 = item1.getType();
        String itemId2 = item2.getItemId();
        String itemType2 = item2.getType();
        ItemRevision itemRevision = itemRevisionService.getItemRevision(item2.getCurrentRevisionId());
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = itemRevision.getLocales().get(defaultLocale);
        String packageName = itemRevision.getPackageName();
        String name = itemRevisionLocaleProperties.getName();
        String longReleaseNotes = itemRevisionLocaleProperties.getReleaseNotes().getLongNotes();
        String shortReleaseNotes = itemRevisionLocaleProperties.getReleaseNotes().getShortNotes();
        String longDescription = itemRevisionLocaleProperties.getLongDescription();
        String shortDescription = itemRevisionLocaleProperties.getShortDescription();

        //release item1
        item1 = releaseItem(item1);

        //test AND
        //%20: space
        buildSearchQuery("itemId:" + itemId1 + "%20AND%20type:" + itemType1, 1, itemId1);
        buildSearchQuery("itemId:" + itemId1 + "%20AND%20" + itemType1, 1, itemId1);
        buildSearchQuery("itemId:" + itemId1 + "%20AND%20" + itemType2, 0);

        //gameMode
        String userInteractionMode = itemRevision.getUserInteractionModes().get(0);
        buildSearchQuery("userInteractionMode:" + userInteractionMode + "%20AND%20itemId:" + itemId2, 1, itemId2);


        //default -- gameMode
        buildSearchQuery(userInteractionMode + "%20AND%20itemId:" + itemId2, 1, itemId2);

        //platform
        List<String> platform = itemRevision.getPlatforms();
        if (!platform.isEmpty()) {
            buildSearchQuery("platform:" + platform.get(0) + "%20AND%20itemId:" + itemId1, 0);
        }

        //default -- platform
        if (!platform.isEmpty()) {
            buildSearchQuery(platform.get(0) + "%20AND%20itemId:" + itemId1, 0);
            buildSearchQuery(platform.get(0) + "%20AND%20itemId:" + itemId2, 1, itemId2);
        }

        //test OR
        buildSearchQuery("itemId:" + itemId1 + "%20OR%20revisionId:" + item2.getCurrentRevisionId(), 2, itemId1, itemId2);
        buildSearchQuery("itemId:" + itemId1 + "%20OR%20itemId:" + itemId2, 2, itemId1, itemId2);
        buildSearchQuery("itemId:" + itemId1 + "%20OR%20packageName:" + packageName, 2, itemId1, itemId2);
        buildSearchQuery("name:" + name + "%20OR%20longNotes:" + longReleaseNotes, 1, itemId2);
        buildSearchQuery(itemId1 + "%20OR%20name:" + name + "%20OR%20shortNotes:" + shortReleaseNotes, 2, itemId1, itemId2);
        buildSearchQuery(longDescription + "%20AND%20" + shortDescription, 1, itemId2);
        buildSearchQuery(longDescription + "%20OR%20" + shortDescription, 1, itemId2);
    }

    private void buildSearchQuery(String queryOption, int expectedRtnSize, String... itemId) throws Exception {
        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> query = new ArrayList<>();

        query.add(queryOption);
        paraMap.put("q", query);
        verifyGetItemsScenarios(paraMap, expectedRtnSize, itemId);
    }

}
