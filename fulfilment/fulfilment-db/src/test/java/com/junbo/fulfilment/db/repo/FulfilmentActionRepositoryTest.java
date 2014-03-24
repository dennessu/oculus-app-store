package com.junbo.fulfilment.db.repo;

import com.junbo.fulfilment.db.BaseTest;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;
import com.junbo.fulfilment.spec.constant.FulfilmentStatus;
import com.junbo.fulfilment.spec.fusion.CatalogEntityType;
import com.junbo.fulfilment.spec.fusion.LinkedEntry;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FulfilmentActionRepositoryTest extends BaseTest {
    @Autowired
    private FulfilmentActionRepository repo;

    @Test
    public void testCreate() {
        FulfilmentAction action = buildFulfilmentAction();
        repo.create(action);
        Assert.assertNotNull(action.getActionId(), "Entity id should not be null.");
    }

    @Test
    public void testGet() {
        FulfilmentAction action = buildFulfilmentAction();
        repo.create(action);

        FulfilmentAction retrieved = repo.get(action.getActionId());
        Assert.assertEquals(retrieved.getResult(), action.getResult(), "[result] should match.");
        Assert.assertEquals(retrieved.getType(), action.getType(), "[type] should match.");
        Assert.assertEquals(retrieved.getStatus(), action.getStatus(), "[status] should match.");
    }

    @Test
    public void testUpdate() {
        FulfilmentAction action = buildFulfilmentAction();
        repo.create(action);

        String updatedStatus = FulfilmentStatus.SUCCEED;
        String updatedResult = "/entitlements/1234";
        repo.update(action.getActionId(), updatedStatus, updatedResult);

        FulfilmentAction retrieved = repo.get(action.getActionId());
        Assert.assertEquals(retrieved.getStatus(), updatedStatus, "[status] should match.");
        Assert.assertEquals(retrieved.getResult(), updatedResult, "[result] should match.");
    }

    private FulfilmentAction buildFulfilmentAction() {
        FulfilmentAction action = new FulfilmentAction();
        action.setFulfilmentId(123L);
        action.setResult("test result");
        action.setStatus(FulfilmentStatus.SUCCEED);
        action.setType(FulfilmentActionType.GRANT_ENTITLEMENT);
        action.setCopyCount(10);

        List<LinkedEntry> items = new ArrayList();
        LinkedEntry item1 = new LinkedEntry();
        item1.setEntityType(CatalogEntityType.ITEM);
        item1.setId(100L);
        item1.setSku(111L);
        item1.setQuantity(100000);

        LinkedEntry item2 = new LinkedEntry();
        item2.setEntityType(CatalogEntityType.ITEM);
        item2.setId(200L);
        item2.setSku(222L);
        item2.setQuantity(200000);

        items.add(item1);
        items.add(item2);

        action.setItems(items);

        Map<String, Object> props = new HashMap();
        props.put("key1", "value1");
        props.put("key2", "value2");
        action.setProperties(props);

        return action;
    }
}
