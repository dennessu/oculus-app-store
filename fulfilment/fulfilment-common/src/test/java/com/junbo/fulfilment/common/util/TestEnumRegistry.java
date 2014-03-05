package com.junbo.fulfilment.common.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestEnumRegistry {
    @Test
    public void testBVT() {
        FulfilmentStatus status = EnumRegistry.resolve(100, FulfilmentStatus.class);
        Assert.assertEquals(status, FulfilmentStatus.PENDING);

        status = EnumRegistry.resolve(200, FulfilmentStatus.class);
        Assert.assertEquals(status, FulfilmentStatus.SUCCEED);

        status = EnumRegistry.resolve(-999, FulfilmentStatus.class);
        Assert.assertEquals(status, FulfilmentStatus.FAILED);

        status = EnumRegistry.resolve(77777777, FulfilmentStatus.class);
        Assert.assertEquals(status, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFailure() {
        EnumRegistry.resolve(100, FulfilmentType.class);
    }
}
