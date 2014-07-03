package com.junbo.fulfilment.db.repo;

import com.junbo.fulfilment.db.BaseTest;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FulfilmentRepositoryTest extends BaseTest {
    @Autowired
    private FulfilmentRepository repo;

    @Test
    public void testCreate() {
        FulfilmentItem item = buildFulfilmentItem();
        repo.create(item);

        Assert.assertNotNull(item.getFulfilmentId(), "Entity id should not be null.");
    }

    private FulfilmentItem buildFulfilmentItem() {
        FulfilmentItem item = new FulfilmentItem();
        item.setRequestId(generateLong());
        item.setItemReferenceId(100L);
        item.setOfferId("12345L");
        item.setTimestamp(System.currentTimeMillis());
        item.setQuantity(10);

        return item;
    }
}
