/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.dao;

import com.junbo.catalog.db.BaseTest;
import com.junbo.catalog.db.entity.OfferEntity;
import com.junbo.catalog.spec.model.offer.OffersGetOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class OfferDaoTest extends BaseTest {
    @Autowired
    private OfferDao offerDao;

    @Test
    public void testCreateAndGet() {
        OfferEntity entity = buildOfferEntity();
        //List<Long> res = Utils.fromJson(Utils.toJson(new ArrayList<Long>()), List.class);
        //System.out.println(res);
        //System.out.println(Utils.toJson(Utils.toJson(new ArrayList<Long>())));
        offerDao.create(entity);
       /* OffersGetOptions options = new OffersGetOptions();
        options.setCategory(5L);
        options.setStart(3);
        options.setSize(2);

        List<OfferEntity> offers = offerDao.getOffers(options);
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println(offers.size());

        for (OfferEntity offerEntity : offers) {
            System.out.println(offerEntity.getOfferId());
        }
        Assert.assertNotNull(offers, "Entity should not be null.");
        */
        Assert.assertNotNull(offerDao.get(entity.getId()), "Entity should not be null.");

       // Assert.assertNotNull(offer, "Entity should not be null.");
        //System.out.println(offerDao.get(entity.getId()).getCategories());
    }

    private OfferEntity buildOfferEntity() {
        OfferEntity entity = new OfferEntity();
        entity.setOfferId(generateId());
        entity.setPublished(false);
        entity.setOwnerId(generateId());
        entity.setCurrentRevisionId(1L);
        entity.setCategories(Arrays.asList(1L, 2L, 8L));

        return entity;
    }
}
