package com.junbo.fulfilment.core.handler;

import com.junbo.fulfilment.core.BaseTest;
import com.junbo.fulfilment.spec.constant.FulfilmentActionType;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HandlerRegistryTest extends BaseTest {
    @Test
    public void testBVT() {
        HandlerRegistry.resolve(FulfilmentActionType.GRANT_ENTITLEMENT);
        HandlerRegistry.resolve(FulfilmentActionType.DELIVER_PHYSICAL_GOODS);
        HandlerRegistry.resolve(FulfilmentActionType.CREDIT_WALLET);

        try {
            HandlerRegistry.resolve("UNKNOWN_ACTION_TYPE");

            Assert.fail("No corresponding handler.");
        } catch (IllegalStateException e) {
            // good
        }
    }
}
