/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemRevision;

import com.junbo.test.common.apihelper.identity.impl.OrganizationServiceImpl;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.enums.CatalogItemType;
import com.junbo.test.catalog.impl.ItemServiceImpl;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.catalog.spec.model.item.Item;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.common.id.OrganizationId;
import com.junbo.test.catalog.ItemService;
import com.junbo.test.common.property.*;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Jason
 * Time: 7/4/2014
 * For testing catalog post item revision API
 */
public class TestPutItemRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPutItemRevision.class);

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

        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision(item1);

        //update status
        itemRevision.setStatus(CatalogEntityStatus.REJECTED.name());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        itemRevision.setStatus(CatalogEntityStatus.PENDING_REVIEW.name());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        itemRevision.setStatus(CatalogEntityStatus.DELETED.name());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);

        itemRevision.setStatus(CatalogEntityStatus.APPROVED.name());
        itemRevision = itemRevisionService.updateItemRevision(itemRevision.getRevisionId(), itemRevision);
    }

}
