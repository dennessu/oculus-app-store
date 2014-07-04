package com.junbo.fulfilment.db.dao;


import com.junbo.fulfilment.db.BaseTest;
import com.junbo.fulfilment.db.entity.FulfilmentRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.UUID;


public class FulfilmentRequestDaoTest extends BaseTest {
    @Autowired
    private FulfilmentRequestDao dao;

    @Test
    public void testCreate() {
        FulfilmentRequestEntity entity = buildFulfilmentRequest();
        dao.create(entity);

        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        FulfilmentRequestEntity entity = buildFulfilmentRequest();
        dao.create(entity);

        Assert.assertNotNull(dao.get(entity.getId()), "Entity should not be null.");
    }

    @Test
    public void testExists() {
        FulfilmentRequestEntity entity = buildFulfilmentRequest();
        dao.create(entity);

        Assert.assertTrue(dao.exists(entity.getId()), "Entity should exist.");
    }

    @Test
    public void testFindByTrackingUuid() {
        FulfilmentRequestEntity entity = buildFulfilmentRequest();
        dao.create(entity);

        UUID trackingUuid = entity.getTrackingUuid();
        FulfilmentRequestEntity retrieved = dao.findByTrackingUuid(entity.getOrderId(), trackingUuid.toString());

        Assert.assertEquals(retrieved.getId(), entity.getId(), "The fulfilment entity should be the same.");
    }

    @Test
    public void testFindByBillingOrderId() {
        FulfilmentRequestEntity entity = buildFulfilmentRequest();
        dao.create(entity);

        Long billingOrderId = entity.getOrderId();
        FulfilmentRequestEntity retrieved = dao.findByOrderId(billingOrderId);

        Assert.assertEquals(retrieved.getId(), entity.getId(), "The fulfilment entity should be the same.");
    }

    private FulfilmentRequestEntity buildFulfilmentRequest() {
        FulfilmentRequestEntity entity = new FulfilmentRequestEntity();

        entity.setTrackingUuid(generateUUID());
        entity.setUserId(generateLong());
        entity.setOrderId(generateLong());
        entity.setPayload("{\"key\": \"test_key\"}");

        entity.setCreatedBy("ut");
        entity.setCreatedTime(new Date());

        return entity;
    }
}