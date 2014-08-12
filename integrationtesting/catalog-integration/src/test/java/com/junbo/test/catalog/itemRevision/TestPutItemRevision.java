/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemRevision;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.catalog.spec.model.item.ItemRevisionLocaleProperties;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Binary;
import com.junbo.test.common.libs.RandomFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

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
public class TestPutItemRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPutItemRevision.class);

    private Item item1;
    private Item item2;
    private Organization organization;
    private OrganizationId organizationId;
    private final String defaultLocale = "en_US";

    private ItemService itemService = ItemServiceImpl.instance();
    private ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organization = organizationService.postDefaultOrganization();
        organizationId = organization.getId();

        item1 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
    }

    @Property(
            priority = Priority.BVT,
            features = "Put v1/item-revisions/{itemRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put item revision successfully",
            steps = {
                    "1. Prepare a default item revision",
                    "2. Put the item revision with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutItemRevision() throws Exception {
        this.prepareTestData();
        prepareCatalogAdminToken();

        OrganizationService organizationService = OrganizationServiceImpl.instance();
        Organization organizationTmp = organizationService.postDefaultOrganization();
        OrganizationId organizationIdTmp = organizationTmp.getId();

        Master.getInstance().setCurrentUid(IdConverter.idToHexString(organization.getOwnerId()));
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item1);

        //update status
        itemRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        itemRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        itemRevision.setStatus(CatalogEntityStatus.APPROVED.name());
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update item field
        item1 = itemService.getItem(item1.getItemId());

        itemRevision = itemRevisionService.postDefaultItemRevision(item1);
        itemRevision.setItemId(item2.getItemId());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update developer field
        Master.getInstance().setCurrentUid(IdConverter.idToHexString(organizationTmp.getOwnerId()));
        item2.setOwnerId(organizationIdTmp);
        itemService.updateItem(item2.getItemId(), item2);

        itemRevision.setOwnerId(organizationIdTmp);
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update platforms
        List<String> platforms = new ArrayList<>();
        platforms.add("PC");
        platforms.add("MAC");
        platforms.add("LINUX");
        platforms.add("ANDROID");
        itemRevision.setPlatforms(platforms);
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update user interaction Modes
        List<String> interactionModes = new ArrayList<>();
        interactionModes.add("SINGLE_USER");
        interactionModes.add("MULTI_USER");
        interactionModes.add("CO_OP");
        itemRevision.setUserInteractionModes(interactionModes);
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update package name
        itemRevision.setPackageName(RandomFactory.getRandomStringOfAlphabet(20));
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update locales
        Map<String, ItemRevisionLocaleProperties> locales = itemRevision.getLocales();
        ItemRevisionLocaleProperties itemRevisionLocaleProperties = locales.get(defaultLocale);

        if (itemRevisionLocaleProperties == null) {
            itemRevisionLocaleProperties = new ItemRevisionLocaleProperties();
        }

        itemRevisionLocaleProperties.setName("testItemRevision_" + RandomFactory.getRandomStringOfAlphabet(10));
        itemRevisionLocaleProperties.setCommunityForumLink("http://www.gooooooogle.com");
        itemRevisionLocaleProperties.setManualDocument("http://www.gooooooogle.com");
        itemRevisionLocaleProperties.setSupportEmail("aaa@wansan.com");

        locales.put(defaultLocale, itemRevisionLocaleProperties);

        itemRevision.setLocales(locales);
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update sku
        itemRevision.setSku(RandomFactory.getRandomStringOfAlphabet(20));
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //update distributionChannels
        List<String> channels = new ArrayList<>();
        channels.add("Store");
        itemRevision.setDistributionChannels(channels);
        itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Put v1/item-revisions/{itemRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put item revision successfully",
            steps = {
                    "1. Prepare a default item revision",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutItemRevisionInvalidScenarios() throws Exception {
        this.prepareTestData();

        Master.getInstance().setCurrentUid(IdConverter.idToHexString(organization.getOwnerId()));
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item1);
        ItemRevision itemRevisionTmp = itemRevisionService.postDefaultItemRevision(item1);

        String itemRevisionId = itemRevision.getRevisionId();
        String itemRevisionRev = itemRevision.getRev();

        //not existed id
        String invalidRevisionId = "AAAA";
        verifyExpectedFailure(invalidRevisionId, itemRevision, 404);

        //existed but not its revision id
        verifyExpectedFailure(itemRevisionTmp.getRevisionId(), itemRevision);

        //update revision id
        itemRevision.setRevisionId(invalidRevisionId);
        verifyExpectedFailure(itemRevisionId, itemRevision);

        itemRevision.setRevisionId(itemRevisionTmp.getRevisionId());
        verifyExpectedFailure(itemRevisionId, itemRevision);

        itemRevision.setRevisionId(itemRevisionId);

        //update rev
        itemRevision.setRev(RandomFactory.getRandomStringOfAlphabet(10));
        verifyExpectedFailure(itemRevisionId, itemRevision);

        itemRevision.setRev(itemRevisionRev);

        //set status to null or invalid string(not enum value)
        itemRevision.setStatus(null);
        verifyExpectedFailure(itemRevisionId, itemRevision);

        itemRevision.setStatus("invalidEnumValue");
        verifyExpectedFailure(itemRevisionId, itemRevision);

        itemRevision.setStatus(CatalogEntityStatus.DRAFT.name());

        //set publisher to null
        itemRevision.setOwnerId(null);
        verifyExpectedFailure(itemRevisionId, itemRevision);

        itemRevision.setOwnerId(organizationId);

        //ItemId to null or not existed value or to another item
        itemRevision.setItemId(null);
        verifyExpectedFailure(itemRevisionId, itemRevision);

        itemRevision.setItemId(invalidRevisionId);
        verifyExpectedFailure(itemRevisionId, itemRevision, 404);

        item2 = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        itemRevision.setItemId(item2.getItemId());
        if (item2.getType().equalsIgnoreCase(CatalogItemType.APP.name()) ||
                item2.getType().equalsIgnoreCase(CatalogItemType.DOWNLOADED_ADDITION.name())) {
            Map<String, Binary> binaries = new HashMap<>();
            Binary binary = new Binary();
            binary.setVersion("1");
            binary.setSize(1024L);
            binary.setMd5("abcdabcdabcdabcdabcdabcdabcdabcd");
            binary.setHref("http://www.google.com/downlaod/angrybird1_0.exe");
            binaries.put("PC", binary);
            itemRevision.setBinaries(binaries);
            itemRevision.setDownloadName(RandomFactory.getRandomStringOfAlphabet(10));
        } else {
            itemRevision.setBinaries(null);
            itemRevision.setDownloadName(null);
        }

        itemRevision = itemRevisionService.updateItemRevision(itemRevisionId, itemRevision);

        //check futureExpansion
        Map<String, JsonNode> futureExpansion = new HashMap<>();

        String str = "{\"subCountry\":\"TX\",\"street1\":\"800 West Campbell Road\"," +
                "\"city\":\"Richardson\",\"postalCode\":\"75080\"," +
                "\"country\":{\"id\":\"US\"}}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode value = mapper.readTree(str);

        futureExpansion.put("Address", value);
        itemRevision.setFutureExpansion(futureExpansion);

        itemRevision.setStatus(CatalogEntityStatus.DRAFT.name());
        verifyExpectedFailure(itemRevisionId, itemRevision);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Post v1/item-revisions",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test the validation for IapHostItem when posting item revisions",
            steps = {
                    "1. Post test item revisions only with required fields",
                    "2. Verify the returned values are the same with prepared",
                    "3. Post test item revisions with optional fields",
                    "4. Verify the returned values are the same with prepared"
            }
    )
    @Test
    public void testPutIapHostItem() throws Exception {
        this.prepareTestData();

        Master.getInstance().setCurrentUid(IdConverter.idToHexString(organization.getOwnerId()));
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item1);

        //Set Distribution Channel to include INAPP:
        List<String> distributionChannels = new ArrayList<>();
        distributionChannels.add("INAPP");
        distributionChannels.add("STORE");

        itemRevision.setDistributionChannels(distributionChannels);

        //Set IapHostItemIds
        List<String> iapHostItemIds = new ArrayList<>();
        iapHostItemIds.add("invalidItemId");

        itemRevision.setIapHostItemIds(iapHostItemIds);

        //should be successful if status is draft, rejected
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        itemRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        //should throw an exception if status is Pending_review or Approved.
        try {
            itemRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
            itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }

        try {
            itemRevision.setStatus(CatalogEntityStatus.APPROVED.name());
            itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision, 400);
        } catch (Exception ex) {
            logger.logInfo("Expected exception");
        }
    }

    private void verifyExpectedFailure(String itemRevisionId, ItemRevision itemRevision) throws Exception {
        verifyExpectedFailure(itemRevisionId, itemRevision, 400);
    }

    private void verifyExpectedFailure(String itemRevisionId, ItemRevision itemRevision, int expectedResponseCode) throws Exception {
        try {
            itemRevisionService.updateItemRevision(itemRevisionId, itemRevision, expectedResponseCode);
            Assert.fail("should return expected exception");
        }
        catch (Exception ex) {
            logger.logInfo("Expected exception: " + ex);
        }
    }

}
