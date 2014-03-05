package com.junbo.fulfilment.common;

import com.junbo.fulfilment.common.util.Cache;
import com.junbo.fulfilment.common.util.Func;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CacheTest {
    @Test
    public void testBVT() {
        final String testCacheValue = "test_cache_value";

        String getCacheValue = Cache.INSTANCE.get("test", new Func<String, String>() {
            @Override
            public String apply(String input) {
                return testCacheValue;
            }
        });

        Assert.assertEquals(getCacheValue, testCacheValue, "cache value should match");
    }
}
