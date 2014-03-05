/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.OfferEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class OfferDaoTest extends BaseTest {
    @Autowired
    private OfferDao offerDao;

    @Test
    public void testCreateAndGet() {
        OfferEntity entity = buildOfferHistoryEntity();
        offerDao.create(entity);
        Assert.assertNotNull(offerDao.get(entity.getId()), "Entity should not be null.");
    }

    @Test
    public void testGetRevisions() {
        OfferEntity entity = buildOfferHistoryEntity();
        offerDao.create(entity);
        List<Integer> revisions = offerDao.getRevisions(entity.getOfferId());
        Assert.assertNotNull(revisions);
        Assert.assertEquals(revisions.size(), 1, "There should 1 revision.");
        Assert.assertEquals(revisions.get(0), Constants.INITIAL_CREATION_REVISION, "The revision should match.");
    }

    private OfferEntity buildOfferHistoryEntity() {
        OfferEntity entity = new OfferEntity();
        entity.setId(generateId());
        entity.setOfferId(generateId());
        entity.setName("test");
        entity.setStatus("test");
        entity.setRevision(1);
        entity.setOwnerId(generateId());
        entity.setPayload("{\"name\": \"test\"}");

        return entity;
    }
}
