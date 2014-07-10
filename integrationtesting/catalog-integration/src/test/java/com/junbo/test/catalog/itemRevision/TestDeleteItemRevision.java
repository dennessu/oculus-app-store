/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.itemRevision;

import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.test.catalog.ItemRevisionService;
import com.junbo.test.catalog.impl.ItemRevisionServiceImpl;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Jason
 * Time: 5/22/2014
 * For testing catalog delete item revision(s) API
 */
public class TestDeleteItemRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestDeleteItemRevision.class);

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/item-revisions/{itemRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an item revision by item revision Id",
            steps = {
                    "1. Prepare an item revision",
                    "2. Delete it and verify can't search it",
                    "3. Post another item revision, attach to an item and release it",
                    "4. Delete the item revision",
                    "5. Verify the currentRevisionId of item is null"
            }
    )
    @Test
    public void testDeleteItemRevision() throws Exception {
        ItemRevisionService itemRevisionService = ItemRevisionServiceImpl.instance();

        //Prepare an item revision
        ItemRevision itemRevision = itemRevisionService.postDefaultItemRevision();
        itemRevisionService.deleteItemRevision(itemRevision.getRevisionId());

        //Try to get the item, expected status code is 404.
        try {
            itemRevisionService.getItemRevision(itemRevision.getRevisionId(), 404);
            Assert.fail("Couldn't find the deleted item revision");
        }
        catch (Exception ex)
        {
        }

        String invalidId = "0L";
        //delete non-existing item
        itemRevision = itemRevisionService.postDefaultItemRevision();
        itemRevisionService.deleteItemRevision(invalidId, 404);
        ItemRevision itemRevisionGet = itemRevisionService.getItemRevision(itemRevision.getRevisionId());
        Assert.assertNotNull(itemRevisionGet);

        releaseItemRevision(itemRevision);

        //delete released item revision should be prohibited.
        itemRevisionService.deleteItemRevision(itemRevision.getRevisionId(), 412);
        itemRevisionGet = itemRevisionService.getItemRevision(itemRevision.getRevisionId());
        Assert.assertNotNull(itemRevisionGet);
    }

}

