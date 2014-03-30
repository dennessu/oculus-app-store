/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.OfferEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class OfferDaoTest extends BaseTest {
    @Autowired
    private OfferDao offerDao;

    @Test
    public void testCreateAndGet() {
        OfferEntity entity = buildOfferEntity();
        List<Long> res = Utils.fromJson(Utils.toJson(new ArrayList<Long>()), List.class);
        System.out.println(res);
        System.out.println(Utils.toJson(Utils.toJson(new ArrayList<Long>())));
        //offerDao.create(entity);
        //Assert.assertNotNull(offerDao.get(entity.getId()), "Entity should not be null.");
        //System.out.println(offerDao.get(entity.getId()).getCategories());
    }

    private OfferEntity buildOfferEntity() {
        OfferEntity entity = new OfferEntity();
        entity.setOfferId(generateId());
        entity.setOfferName("test");
        entity.setStatus("test");
        entity.setOwnerId(generateId());
        entity.setCurrentRevisionId(1L);
        entity.setCategories(null);

        return entity;
    }
}
