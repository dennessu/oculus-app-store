/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.item;

import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.impl.OfferServiceImpl;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.catalog.OfferService;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jason
 * Time: 4/10/2014
 * For testing catalog put item(s) API
 */
public class TestPutItem extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetItem.class);
    private ItemService itemService = ItemServiceImpl.instance();
    private OrganizationId organizationId;
    private Organization organization;

    private void prepareTestData() throws Exception {
        OrganizationService organizationService = OrganizationServiceImpl.instance();
        organization = organizationService.postDefaultOrganization();
        organizationId = organization.getId();
    }

    @Property(
            priority = Priority.BVT,
            features = "Put v1/items/{itemId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put item successfully",
            steps = {
                    "1. Prepare a default item",
                    "2. Put the item with corrected fields values",
                    "3. Verify the action could be successful"
            }
    )
    @Test
    public void testPutItem() throws Exception {
        this.prepareTestData();

        //Prepare an item
        Item item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        //put item
        OfferService offerService = OfferServiceImpl.instance();
        Offer offer = offerService.postDefaultOffer(organizationId);

        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();
        ItemAttribute itemAttribute1 = itemAttributeService.postDefaultItemAttribute();
        ItemAttribute itemAttribute2 = itemAttributeService.postDefaultItemAttribute();
        List<String> genres = new ArrayList<>();
        genres.add(itemAttribute1.getId());
        genres.add(itemAttribute2.getId());

        item.setGenres(genres);
        item.setDefaultOffer(offer.getOfferId());

        Item itemPut = itemService.updateItem(item.getItemId(), item);

        //Verification
        Assert.assertEquals(itemPut.getGenres(), genres);
        Assert.assertEquals(itemPut.getDefaultOffer(), offer.getOfferId());
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Put v1/items/{itemId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test put item successfully",
            steps = {
                    "1. Prepare a default item",
                    "2. test invalid values(like null, not null and some invalid enum values)",
                    "3. Verify the expected error"
            }
    )
    @Test
    public void testPutItemInvalidScenarios() throws Exception {
        this.prepareTestData();

        List<String> genresCategory = new ArrayList<>();
        List<String> genresInvalid = new ArrayList<>();
        genresInvalid.add("0L");
        genresInvalid.add("1L");

        //Prepare an item
        Item item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        //update itself id
        String itemId = item.getItemId();
        item.setItemId("1L");
        verifyExpectedError(itemId, item);

        //test rev
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item.setRev("revValue");
        verifyExpectedError(item.getItemId(), item);

        //can't update current revision id
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item.setCurrentRevisionId("0L");
        verifyExpectedError(item.getItemId(), item);

        //can't update developerId
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);

        OrganizationService organizationService = OrganizationServiceImpl.instance();
        Organization organizationTmp = organizationService.postDefaultOrganization();

        item.setOwnerId(organizationTmp.getId());
        verifyExpectedError(item.getItemId(), item);

        //can't update current revision id
        Master.getInstance().setCurrentUid(IdConverter.idToHexString(organization.getOwnerId()));
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item.setCurrentRevisionId("0L");
        verifyExpectedError(item.getItemId(), item);

        //test type is invalid enums
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item.setType("invalid type");
        verifyExpectedError(item.getItemId(), item);

        //test defaultOffer is not existed
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item.setDefaultOffer("0L");
        verifyExpectedError(item.getItemId(), item);

        //test genres is not existed
        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item.setGenres(genresInvalid);
        verifyExpectedError(item.getItemId(), item);

        //test genres type is category
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);

        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        genresCategory.add(offerAttribute.getId());

        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        item.setGenres(genresCategory);
        verifyExpectedError(item.getItemId(), item);

        item = itemService.postDefaultItem(CatalogItemType.getRandom(), organizationId);
        if (item.getType().equalsIgnoreCase(CatalogItemType.APP.getItemType())) {
            item.setType(CatalogItemType.PHYSICAL.getItemType());
            verifyExpectedError(item.getItemId(), item);
        }
        else {
            item.setType(CatalogItemType.APP.getItemType());
            verifyExpectedError(item.getItemId(), item);
        }

    }

    private void verifyExpectedError(String itemId, Item item) {
        try {
            //Error code 400 means "Missing Input field", "Unnecessary field found" or "invalid value"
            itemService.updateItem(itemId, item, 400);
            Assert.fail("Put item should fail");
        }
        catch (Exception ex) {
        }
    }

}
