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
 * Time: 8/8/2014
 * For testing catalog get price tier API
 */
public class TestGetPriceTier extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestGetPriceTier.class);
    private PriceTierService priceTierService = PriceTierServiceImpl.instance();

    @Property(
            priority = Priority.Dailies,
            features = "Get v1/price-tiers/{priceTierId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get a price tier by itsId(valid, invalid scenarios)",
            steps = {
                    "1. Prepare a price tier",
                    "2. Get it by Id",
                    "3. Verify not able to get the price tier by wrong Id"
            }
    )
    @Test
    public void testGetAPriceTierById() throws Exception {
        prepareCatalogAdminToken();

        //Prepare a price tier
        PriceTier priceTier = priceTierService.postDefaultPriceTier();
        //get the price tier by Id, assert not null
        PriceTier priceTierRtn = priceTierService.getPriceTier(priceTier.getId());
        Assert.assertNotNull(priceTierRtn, "Can't get price tier");

        //verify the invalid Id scenario
        String invalidId = "0000";
        try {
            priceTierService.getPriceTier(invalidId, 404);
            Assert.fail("Shouldn't get price tier with wrong id");
        } catch (Exception e) {
            logger.logInfo("Expected exception: couldn't get price tier  with wrong id");
        }
    }

    @Property(
            priority = Priority.Comprehensive,
            features = "Get v1/price-tiers?tierId=&tierId=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get price tier(s) by Id(s)(valid, invalid scenarios)",
            steps = {
                    "1. Prepare 3 price tiers",
                    "2. Get the price tiers by their ids"
            }
    )
    @Test
    public void testGetPriceTiersByIds() throws Exception {
        prepareCatalogAdminToken();

        HashMap<String, List<String>> paraMap = new HashMap<>();
        List<String> listPriceTierId = new ArrayList<>();

        //Prepare some price tiers
        PriceTier priceTier1 = priceTierService.postDefaultPriceTier();
        PriceTier priceTier2 = priceTierService.postDefaultPriceTier();
        PriceTier priceTier3 = priceTierService.postDefaultPriceTier();

        listPriceTierId.add(priceTier1.getId());
        paraMap.put("tierId", listPriceTierId);
        verifyGetPriceTiers(paraMap, 1, priceTier1);

        listPriceTierId.add(priceTier2.getId());
        paraMap.put("tierId", listPriceTierId);
        verifyGetPriceTiers(paraMap, 2, priceTier1, priceTier2);

        listPriceTierId.add(priceTier3.getId());
        paraMap.put("tierId", listPriceTierId);
        verifyGetPriceTiers(paraMap, 3, priceTier1, priceTier2, priceTier3);

        listPriceTierId.clear();
        listPriceTierId.add("0000000000");
        listPriceTierId.add(priceTier2.getId());
        listPriceTierId.add(priceTier3.getId());
        paraMap.put("tierId", listPriceTierId);
        verifyGetPriceTiers(paraMap, 2, priceTier2, priceTier3);

        listPriceTierId.clear();
        listPriceTierId.add("0000000000");
        listPriceTierId.add("0000000001");
        listPriceTierId.add("0000000002");
        paraMap.put("tierId", listPriceTierId);
        verifyGetPriceTiers(paraMap, 0);
    }

    private void verifyGetPriceTiers(HashMap<String, List<String>> paraMap, int expectedRtnSize, PriceTier... PriceTiers)
            throws Exception {
        Results<PriceTier> priceTiersGet = priceTierService.getPriceTiers(paraMap);

        Assert.assertEquals(priceTiersGet.getItems().size(), expectedRtnSize);
        for (PriceTier tier : PriceTiers) {
            Assert.assertTrue(isContain(priceTiersGet, tier));
        }
    }

}
