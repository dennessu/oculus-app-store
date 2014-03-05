package com.junbo.fulfilment.db.dao;

import com.junbo.fulfilment.db.BaseTest;
import com.junbo.fulfilment.db.entity.FulfilmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

public class FulfilmentDaoTest extends BaseTest {
    @Autowired
    private FulfilmentDao dao;

    @Test
    public void testCreate() {
        FulfilmentEntity entity = buildFulfilment();
        dao.create(entity);

        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        FulfilmentEntity entity = buildFulfilment();
        dao.create(entity);

        Assert.assertNotNull(dao.get(entity.getId()), "Entity should not be null.");
    }

    @Test
    public void testExists() {
        FulfilmentEntity entity = buildFulfilment();
        dao.create(entity);

        Assert.assertTrue(dao.exists(entity.getId()), "Entity should exist.");
    }

    @Test
    public void testFindByRequestId() {
        Long requestId = generateLong();

        FulfilmentEntity entity1 = buildFulfilment();
        entity1.setRequestId(requestId);
        dao.create(entity1);

        FulfilmentEntity entity2 = buildFulfilment();
        entity2.setRequestId(requestId);
        dao.create(entity2);

        dao.flush();

        List<FulfilmentEntity> results = dao.findByRequestId(requestId);
        Assert.assertEquals(results.size(), 2, "Result size should match.");
    }

    private FulfilmentEntity buildFulfilment() {
        FulfilmentEntity entity = new FulfilmentEntity();

        entity.setRequestId(generateLong());
        entity.setPayload("{\"key\": \"test_key\"}");
        entity.setCreatedBy("ut");
        entity.setCreatedTime(new Date());

        return entity;
    }
}