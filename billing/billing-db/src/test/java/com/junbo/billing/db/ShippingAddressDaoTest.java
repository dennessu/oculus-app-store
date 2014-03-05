/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import com.junbo.billing.db.address.ShippingAddressEntity;
import com.junbo.billing.db.dao.ShippingAddressEntityDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by xmchen on 14-2-7.
 */
public class ShippingAddressDaoTest extends BaseTest {
    @Autowired
    private ShippingAddressEntityDao dao;

    @Test
    public void testInsertGet() {
        ShippingAddressEntity entity = buildShippingAddress(generateId());
        dao.insert(entity);
        dao.flush();

        ShippingAddressEntity returned = dao.get(entity.getAddressId());

        Assert.assertNotNull(returned.getAddressId(), "Entity id should not be null.");
        Assert.assertEquals(returned.getFirstName(), entity.getFirstName(), "Entity inserted and get should be same.");
    }

    @Test
    public void testUpdate() {
        ShippingAddressEntity entity = buildShippingAddress(generateId());
        dao.insert(entity);

        entity.setDeleted(true);
        dao.update(entity);
        dao.flush();

        ShippingAddressEntity returned = dao.get(entity.getAddressId());

        Assert.assertNotNull(returned.getAddressId(), "Entity id should not be null.");
        Assert.assertEquals(returned.getDeleted(), Boolean.TRUE, "Entity inserted and get should be same.");
    }

    @Test
    public void testFindByUserId() {
        Long userId = generateId();
        ShippingAddressEntity entity = buildShippingAddress(userId);
        dao.insert(entity);

        entity = buildShippingAddress(userId);
        dao.insert(entity);
        dao.flush();

        List<ShippingAddressEntity> addrs = dao.findByUserId(userId);

        Assert.assertEquals(addrs.size(), 2, "There should be 2 addresses for this user.");
    }

    @Test
    public void testSoftDelete() {
        Long userId = generateId();
        ShippingAddressEntity entity = buildShippingAddress(userId);
        dao.insert(entity);

        entity = buildShippingAddress(userId);
        dao.insert(entity);
        dao.flush();

        List<ShippingAddressEntity> addrs = dao.findByUserId(userId);

        Assert.assertEquals(addrs.size(), 2, "There should be 2 addresses for this user.");

        dao.softDelete(entity.getAddressId());
        dao.flush();

        addrs = dao.findByUserId(userId);

        Assert.assertEquals(addrs.size(), 1, "There should be 1 address left for this user.");
    }

    private ShippingAddressEntity buildShippingAddress(Long userId) {
        ShippingAddressEntity entity = new ShippingAddressEntity();
        entity.setUserId(userId);

        entity.setAddressId(generateId());
        entity.setRequestorId(generateUUID().toString());
        entity.setFirstName("Xumeng");
        entity.setLastName("Chen");
        entity.setCountry("US");
        entity.setCity("FORREST");
        entity.setState("CA");
        entity.setStreet("NO 1, Dapu RD");
        entity.setPostalCode("96045");

        entity.setCreatedBy("UT");
        entity.setCreatedDate(new Date());

        return entity;
    }
}
