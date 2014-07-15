/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerAttribute;

import com.junbo.catalog.spec.model.attribute.OfferAttribute;
import com.junbo.test.catalog.impl.OfferAttributeServiceImpl;
import com.junbo.test.catalog.OfferAttributeService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.oauth.OAuthTokenService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthTokenServiceImpl;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Jason
 * Time: 4/15/2014
 * For testing catalog delete offer attribute(s) API
 */
public class TestDeleteOfferAttribute extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestDeleteOfferAttribute.class);

    @BeforeClass
    private void PrepareTestData() throws Exception {
        OAuthTokenService oAuthTokenService = OAuthTokenServiceImpl.getInstance();
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOG);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/offer-attributes/{offerAttributeId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an offer attribute by its Id",
            steps = {
                    "1. Prepare an offer attribute",
                    "2. delete it and verify can't search it"
            }
    )
    @Test
    public void testDeleteOfferAttribute() throws Exception {
        OfferAttributeService offerAttributeService = OfferAttributeServiceImpl.instance();

        //Prepare an offer and delete it
        OfferAttribute offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        offerAttributeService.deleteOfferAttribute(offerAttribute.getId());

        //Try to get the offer, expected status code is 404.
        try {
            offerAttributeService.getOfferAttribute(offerAttribute.getId(), 404);
            Assert.fail("Couldn't find the deleted offer attribute");
        }
        catch (Exception ex)
        {
        }

        //delete non-existing offer
        String invalidId = "0L";
        offerAttribute = offerAttributeService.postDefaultOfferAttribute();
        offerAttributeService.deleteOfferAttribute(invalidId, 404);
        offerAttribute = offerAttributeService.getOfferAttribute(offerAttribute.getId());
        Assert.assertNotNull(offerAttribute);
    }
}
