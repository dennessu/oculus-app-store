/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.OfferDraftEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class OfferDraftDaoTest extends BaseTest {
    @Autowired
    private OfferDraftDao offerDraftDao;

    @Test
    public void testCreate() {
        OfferDraftEntity entity = buildOfferEntity();
        Long id = offerDraftDao.create(entity);
        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
        Assert.assertNotNull(id, "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        OfferDraftEntity entity = buildOfferEntity();
        offerDraftDao.create(entity);
        Assert.assertNotNull(offerDraftDao.get(entity.getId()), "Entity should not be null.");
    }

    @Test
    public void testGetOffers() {
        OfferDraftEntity entity = buildOfferEntity();
        offerDraftDao.create(entity);
        List<OfferDraftEntity> offers = offerDraftDao.getOffers(0,10);
        Assert.assertNotNull(offers);
    }

    @Test
    public void testExists() {
        OfferDraftEntity entity = buildOfferEntity();
        offerDraftDao.create(entity);
        Assert.assertTrue(offerDraftDao.exists(entity.getId()), "Entity should exist.");
    }

    @Test
    public void testUpdate() {
        OfferDraftEntity entity = buildOfferEntity();
        offerDraftDao.create(entity);
        OfferDraftEntity retrieved = offerDraftDao.get(entity.getId());
        retrieved.setStatus("PUBLISHED");
        offerDraftDao.update(retrieved);
        Assert.assertEquals(retrieved.getStatus(), "PUBLISHED", "Status should have changed.");
    }

    private OfferDraftEntity buildOfferEntity() {
        OfferDraftEntity entity = new OfferDraftEntity();
        entity.setId(generateId());
        entity.setName("test");
        entity.setStatus("test");
        entity.setRevision(1);
        entity.setOwnerId(generateId());
        entity.setPayload("{\"name\": \"test\"}");

        return entity;
    }
}
