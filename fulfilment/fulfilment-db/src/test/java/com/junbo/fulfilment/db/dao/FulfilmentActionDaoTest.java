package com.junbo.fulfilment.db.dao;

import com.junbo.fulfilment.db.BaseTest;
import com.junbo.fulfilment.db.entity.FulfilmentActionType;
import com.junbo.fulfilment.db.entity.FulfilmentActionEntity;
import com.junbo.fulfilment.db.entity.FulfilmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Date;

public class FulfilmentActionDaoTest extends BaseTest {
    @Autowired
    private FulfilmentActionDao dao;

    @Test
    public void testCreate() {
        FulfilmentActionEntity entity = buildFulfilmentAction();
        dao.create(entity);

        Assert.assertNotNull(entity.getId(), "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        FulfilmentActionEntity entity = buildFulfilmentAction();
        dao.create(entity);

        Assert.assertNotNull(dao.get(entity.getId()), "Entity should not be null.");
    }

    private FulfilmentActionEntity buildFulfilmentAction() {
        FulfilmentActionEntity entity = new FulfilmentActionEntity();

        entity.setStatus(FulfilmentStatus.SUCCEED);
        entity.setType(FulfilmentActionType.GRANT_ENTITLEMENT);
        entity.setFulfilmentId(generateLong());
        entity.setPayload("{\"key\": \"test_key\"}");
        entity.setCreatedBy("ut");
        entity.setCreatedTime(new Date());

        return entity;
    }
}