/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.memcached;

import com.junbo.apphost.core.health.ConnectionInfoProvider;
import net.spy.memcached.MemcachedNode;

import java.util.HashMap;
import java.util.Map;

/**
 * MemcachedClientConnectionInfoProvider.
 */
public class MemcachedClientConnectionInfoProvider implements ConnectionInfoProvider {

    @Override
    public Map getConnectionInfo() {
        Map<String, String> result = new HashMap<>();
        JunboMemcachedClient memcachedClient = JunboMemcachedClient.instance();
        if (memcachedClient != null) {
            int i = 0;
            for (MemcachedNode memcachedNode : memcachedClient.getNodeLocator().getAll()) {
                result.put("node " + i++, "(" + (memcachedNode.isActive() ? "active) " : "inactive) ")
                        + memcachedNode.toString());
            }
        }
        return result;
    }
}
