/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.catalog.offerRevision;

import com.junbo.test.catalog.enums.CatalogEntityStatus;
import com.junbo.test.catalog.impl.OfferRevisionServiceImpl;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.test.catalog.OfferRevisionService;
import com.junbo.test.catalog.util.BaseTestClass;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.common.libs.LogHelper;

import org.testng.annotations.Test;
import org.testng.Assert;

/**
 * @author Jason
 * Time: 5/22/2014
 * For testing catalog delete offer revision(s) API
 */
public class TestDeleteOfferRevision extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestDeleteOfferRevision.class);

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/offer-revisions/{offerRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an offer revision by offer revision Id",
            steps = {
                    "1. Prepare an offer revision",
                    "2. Delete it and verify can't search it",
                    "3. Post another offer revision, attach to an offer and release it",
                    "4. Delete the offer revision",
                    "5. Verify the currentRevisionId of offer is null"
            }
    )
    @Test
    public void testDeleteOfferRevision() throws Exception {
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        //Prepare an offer revision
        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision();
        offerRevisionService.deleteOfferRevision(offerRevision.getRevisionId());

        //Try to get the offer, expected status code is 404.
        try {
            offerRevisionService.getOfferRevision(offerRevision.getRevisionId(), null, 404);
            Assert.fail("Couldn't find the deleted offer revision");
        }
        catch (Exception ex)
        {
        }

        String invalidId = "0L";
        //delete non-existing offer
        offerRevision = offerRevisionService.postDefaultOfferRevision();
        offerRevisionService.deleteOfferRevision(invalidId, 404);
        OfferRevision offerRevisionGet = offerRevisionService.getOfferRevision(offerRevision.getRevisionId());
        Assert.assertNotNull(offerRevisionGet);

        releaseOfferRevision(offerRevision);

        //delete released offer revision should be prohibited.
        offerRevisionService.deleteOfferRevision(offerRevision.getRevisionId(), 412);
        offerRevisionGet = offerRevisionService.getOfferRevision(offerRevision.getRevisionId());
        Assert.assertNotNull(offerRevisionGet);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Delete v1/offer-revisions/{offerRevisionId}",
            component = Component.Catalog,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test delete an offer revision by offer revision Id whose status is obsolete",
            steps = {
                    "1. Prepare an offer revision",
                    "2. Delete it and verify can't search it",
                    "3. Post another offer revision, attach to an offer and release it",
                    "4. Delete the offer revision",
                    "5. Verify the currentRevisionId of offer is null"
            }
    )
    @Test
    public void testDeleteObsoleteOfferRevision() throws Exception {
        OfferRevisionService offerRevisionService = OfferRevisionServiceImpl.instance();

        //Prepare an offer revision
        OfferRevision offerRevision = offerRevisionService.postDefaultOfferRevision();
        offerRevision = releaseOfferRevision(offerRevision);
        offerRevision = obsoleteOfferRevision(offerRevision);

        //delete obsolete offer revision should be prohibited.
        offerRevisionService.deleteOfferRevision(offerRevision.getRevisionId(), 412);
        OfferRevision offerRevisionGet = offerRevisionService.getOfferRevision(offerRevision.getRevisionId());
        Assert.assertNotNull(offerRevisionGet);
        Assert.assertEquals(offerRevisionGet.getStatus(), CatalogEntityStatus.OBSOLETE.name());
    }
}

