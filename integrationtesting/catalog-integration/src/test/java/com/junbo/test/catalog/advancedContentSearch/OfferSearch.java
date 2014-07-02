/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.advancedContentSearch;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jason
 * Time: 6/27/2014
 * For testing catalog offer advanced search
 */
public class OfferSearch extends BaseTestClass {

    private LogHelper logger = new LogHelper(OfferSearch.class);

    private OrganizationId organizationId;
    private Offer offer1;
    private Offer offer2;
    private ItemService itemService = ItemServiceImpl.instance();
    ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();
    private final Integer MAXSIZE = 10000;
    private final String defaultLocale = "en_US";
    private final String defaultDigitalItemRevisionFileName = "defaultDigitalItemRevision";
    private final String defaultPhysicalItemRevisionFileName = "defaultPhysicalItemRevision";
    private final String defaultStoredValueItemRevisionFileName = "defaultStoredValueItemRevision";

    @BeforeClass
    private void PrepareTestData() throws Exception {

    }

    @Property(
            priority = Priority.BVT,
            features = "Get v1/offers?q=",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test Get offers by advanced query",
            steps = {
                    "1. Prepare some offers",
                    "2. Get the offers by advanced query",
                    "3. Release the offers",
                    "4. Get the offers by advanced query again"
            }
    )
    @Test
    public void testGetItemsByOnlyOneOption() throws Exception {

    }

}
