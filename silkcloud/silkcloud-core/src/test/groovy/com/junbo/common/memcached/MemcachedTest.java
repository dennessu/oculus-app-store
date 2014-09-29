/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.memcached;

import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Java doc for TopologyTest.
 */
public class MemcachedTest {
    private static JunboMemcachedClient client = JunboMemcachedClient.instance();

    @Test
    public void testDataAccessPolicies() throws Exception {
        // clear cache first
        client.flush().get();

        Boolean addResult = client.add("test-key", 0, "123456").get();
        Assert.assertTrue(addResult);

        addResult = client.add("test-key", 0, "654321").get();
        Assert.assertFalse(addResult);

        CASValue<String> casValue = (CASValue)client.gets("test-key");

        Assert.assertEquals(casValue.getValue(), "123456");

        casValue = (CASValue)client.gets("hehe");

        Assert.assertNull(casValue);

        casValue = (CASValue)client.gets("test-key");
        CASResponse response = client.cas("test-key", casValue.getCas(), "7");

        Assert.assertEquals(response, CASResponse.OK);
        Assert.assertEquals(client.get("test-key"), "7");

        response = client.cas("test-key", casValue.getCas(), "8");
        Assert.assertEquals(response, CASResponse.EXISTS);

        casValue = (CASValue)client.gets("test-key");
        Assert.assertEquals(casValue.getValue(), "7");

        client.delete("test-key").get();
        response = client.cas("test-key", casValue.getCas(), "hahahahahahaha");

        Assert.assertEquals(response, CASResponse.NOT_FOUND);

        casValue = (CASValue)client.gets("test-key");
        Assert.assertNull(casValue);
    }
}
