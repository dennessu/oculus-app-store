/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemAttribute;

import com.junbo.catalog.spec.model.attribute.ItemAttribute;
import com.junbo.test.catalog.impl.ItemAttributeServiceImpl;
import com.junbo.test.catalog.ItemAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Jason
 * Time: 4/15/2014
 * For testing catalog delete item attribute(s) API
 */
public class TestDeleteItemAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestDeleteItemAttribute.class);

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/item-attributes/{itemAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an item attribute by its Id",
            steps = {
                    "1. Prepare an item attribute",
                    "2. delete it and verify can't search it"
            }
    )
    @Test
    public void testDeleteItemAttribute() throws Exception {
        ItemAttributeService itemAttributeService = ItemAttributeServiceImpl.instance();

        //Prepare an item and delete it
        ItemAttribute itemAttribute = itemAttributeService.postDefaultItemAttribute();
        itemAttributeService.deleteItemAttribute(itemAttribute.getId());

        //Try to get the item, expected status code is 404.
        try {
            itemAttributeService.getItemAttribute(itemAttribute.getId(), 404);
            Assert.fail("Couldn't find the deleted item attribute");
        }
        catch (Exception ex)
        {
        }

        //delete non-existing item
        String invalidId = "0L";
        itemAttribute = itemAttributeService.postDefaultItemAttribute();
        itemAttributeService.deleteItemAttribute(invalidId, 404);
        itemAttribute = itemAttributeService.getItemAttribute(itemAttribute.getId());
        Assert.assertNotNull(itemAttribute);
    }
}
