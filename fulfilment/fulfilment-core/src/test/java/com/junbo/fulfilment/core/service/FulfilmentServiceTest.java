package com.junbo.fulfilment.core.service;

import com.junbo.common.error.AppErrorException;
import com.junbo.fulfilment.core.BaseTest;
import com.junbo.fulfilment.core.FulfilmentService;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class FulfilmentServiceTest extends BaseTest {
    @Autowired
    private FulfilmentService service;

    @Test
    public void testBVT() {
        FulfilmentRequest request = createRequest();
        service.fulfill(request);

        FulfilmentRequest retrieved = service.retrieveRequest(request.getRequestId());
        Assert.assertEquals(retrieved.getTrackingGuid(), request.getTrackingGuid(), "Tracking GUID should match.");
        Assert.assertEquals(retrieved.getItems().size(), request.getItems().size(), "Items size should match.");
    }

    @Test
    public void testDuplicatedTrackingGuid() {
        FulfilmentRequest request = createRequest();
        service.fulfill(request);
        String trackingGuid = request.getTrackingGuid();

        FulfilmentRequest request2 = createRequest();
        request2.setTrackingGuid(trackingGuid);
        request2 = service.fulfill(request2);

        Assert.assertEquals(request2.getRequestId(), request.getRequestId(), "Request id should match.");
    }

    @Test(expectedExceptions = AppErrorException.class)
    public void testDuplicatedBillingOrderId() {
        FulfilmentRequest request = createRequest();
        service.fulfill(request);
        Long billingOrderId = request.getOrderId();

        FulfilmentRequest request2 = createRequest();
        request2.setOrderId(billingOrderId);
        service.fulfill(request2);
    }

    private FulfilmentRequest createRequest() {
        FulfilmentRequest request = new FulfilmentRequest();
        request.setRequester("SYSTEM");
        request.setTrackingGuid(generateUUID().toString());
        request.setUserId(generatedUserId());
        request.setOrderId(generateLong());

        request.setItems(new ArrayList() {{
            add(new FulfilmentItem() {{
                setOfferId(100L);
                setTimestamp(System.currentTimeMillis());
                setQuantity(2);
                setOrderItemId(10000L);
            }});
            add(new FulfilmentItem() {{
                setOfferId(400L);
                setTimestamp(System.currentTimeMillis());
                setQuantity(3);
                setOrderItemId(20000L);
            }});
        }});

        return request;
    }
}
