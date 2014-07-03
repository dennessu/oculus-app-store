package com.junbo.fulfilment.core.service;

import com.junbo.fulfilment.core.BaseTest;
import com.junbo.fulfilment.core.FulfilmentSupport;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;
import com.junbo.fulfilment.spec.model.FulfilmentAction;
import com.junbo.fulfilment.spec.model.FulfilmentItem;
import com.junbo.fulfilment.spec.model.FulfilmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class FulfilmentServiceSupportTest extends BaseTest {
    @Autowired
    private FulfilmentSupport service;

    @Test
    public void testDistill() {
        FulfilmentRequest request = buildFulfilmentRequest();
        service.distill(request);

        Assert.assertEquals(5, request.getItems().get(0).getActions().size(), "Fulfilment action count should match.");
        Assert.assertEquals(1, request.getItems().get(1).getActions().size(), "Fulfilment action count should match.");
    }

    @Test
    public void testStore() {
        FulfilmentRequest request = buildFulfilmentRequest();
        service.distill(request);
        service.store(request);

        Assert.assertNotNull(request.getRequestId(), "FulfilmentId should not be null.");
        for (FulfilmentItem item : request.getItems()) {
            Assert.assertNotNull(item.getFulfilmentId(), "FulfilmentItemId should not be null.");

            for (FulfilmentAction action : item.getActions()) {
                Assert.assertNotNull(action.getActionId(), "FulfilmentActionId should not be null.");
            }
        }
    }

    @Test
    public void testClassify() {
        FulfilmentRequest request = buildFulfilmentRequest();
        service.distill(request);
        service.store(request);
        ClassifyResult result = service.classify(request);
        Assert.assertEquals(result.get(FulfilmentActionType.GRANT_ENTITLEMENT).size(), 2, "Fulfilment action count should match.");
    }

    @Test
    public void testDispatch() {
        FulfilmentRequest request = buildFulfilmentRequest();
        service.distill(request);
        service.store(request);
        ClassifyResult result = service.classify(request);
        service.dispatch(request, result);
    }

    private FulfilmentRequest buildFulfilmentRequest() {
        FulfilmentRequest request = new FulfilmentRequest();
        request.setRequester("SYSTEM");
        request.setTrackingUuid(generateUUID().toString());
        request.setUserId(generateLong());
        request.setOrderId(generateLong());

        request.setItems(new ArrayList() {{
            add(new FulfilmentItem() {{
                setOfferId("100L");
                setTimestamp(System.currentTimeMillis());
                setQuantity(2);
                setItemReferenceId(10000L);
            }});
            add(new FulfilmentItem() {{
                setOfferId("400L");
                setTimestamp(System.currentTimeMillis());
                setQuantity(3);
                setItemReferenceId(20000L);
            }});
        }});

        return request;
    }
}
