package com.junbo.fulfilment.db.repo;

import com.junbo.fulfilment.db.BaseTest;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FulfilmentRequestRepositoryTest extends BaseTest {
    @Autowired
    private FulfilmentRequestRepository repo;

    @Test
    public void testCreate() {
        FulfilmentRequest request = buildFulfilmentRequest();
        repo.create(request);

        Assert.assertNotNull(request.getRequestId(), "Entity id should not be null.");
    }

    @Test
    public void testExistingTrackingUuid() {
        FulfilmentRequest request = buildFulfilmentRequest();
        repo.create(request);
        Assert.assertNotNull(request.getRequestId(), "Entity id should not be null.");

        Long retrievedRequestId = repo.existTrackingUuid(request.getUserId(), request.getTrackingUuid());
        Assert.assertEquals(retrievedRequestId, request.getRequestId(), "Fulfilment request id should match.");
    }

    private FulfilmentRequest buildFulfilmentRequest() {
        FulfilmentRequest request = new FulfilmentRequest();

        request.setTrackingUuid(generateUUID().toString());
        request.setOrderId(generateLong());
        request.setUserId(generateLong());

        return request;
    }
}
