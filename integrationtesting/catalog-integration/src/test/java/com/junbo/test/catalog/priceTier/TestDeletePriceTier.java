/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.priceTier;

import com.junbo.catalog.spec.model.pricetier.PriceTier;
import com.junbo.test.catalog.impl.PriceTierServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.catalog.PriceTierService;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Jason
 * Time: 8/8/2014
 * For testing catalog delet pricetier(s) API
 */
public class TestDeletePriceTier extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestDeletePriceTier.class);

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/price-tiers/{tierId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an price tier by tierId",
            steps = {
                    "1. Prepare a price tier",
                    "2. delete it and verify can't search it"
            }
    )
    @Test
    public void testDeletePriceTier() throws Exception {
        OAuthService oAuthTokenService = OAuthServiceImpl.getInstance();
        PriceTierService pricetierService = PriceTierServiceImpl.instance();

        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.CATALOGADMIN);

        //Prepare an pricetier
        PriceTier pricetier = pricetierService.postDefaultPriceTier();
        String invalidId = "0L";

        pricetierService.deletePriceTier(pricetier.getId());

        //Try to get the pricetier, expected status code is 404.
        try {
            pricetierService.getPriceTier(pricetier.getId(), 404);
            Assert.fail("Couldn't find the deleted pricetier");
        }
        catch (Exception ex)
        {
        }

        //delete non-existing pricetier
        pricetier = pricetierService.postDefaultPriceTier();
        pricetierService.deletePriceTier(invalidId, 404);
        pricetier = pricetierService.getPriceTier(pricetier.getId());
        Assert.assertNotNull(pricetier);
    }

}
