/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.topo;

import com.junbo.common.topo.model.TopologyConfig;
import com.junbo.configuration.ConfigContext;
import com.junbo.configuration.ConfigService;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Java doc for TopologyTest.
 */
public class TopologyTest {
    //@Test
    public void testTopologyConfig() throws Exception {

        ConfigService configService = new ConfigService() {
            private ConfigContext context = new ConfigContext("mdctest", "0", "0.0.0.0/0");

            @Override
            public ConfigContext getConfigContext() {
                return context;
            }

            @Override
            public String getConfigValue(String configKey) {
                return null;
            }

            @Override
            public Properties getAllConfigItems() {
                return new Properties();
            }

            @Override
            public void addListener(String configKey, ConfigListener listener) {

            }
        };

        TopologyConfig topologyConfig = new TopologyConfig(
            "http://0.0.0.0:8081/v1",               // assume I am second app server
            "  https://localhost:8080/v1;0;dc0,\n" +
            "  https://localhost:8180/v1;1;dc1",
            "http://%s/v1",
            "  127.0.0.1:8080;0..3;dc0,\n" +
            "  127.0.0.1:8080;4..7;dc0,\n" +        // test merge
            "  127.0.0.1:8081;8..15;dc0,\n" +
            "  127.0.0.1:8180;0..7;dc1,\n" +
            "  127.0.0.1:8181;8..15;dc1",
            configService
        );

        for (int i = 0; i <= 7; ++i) {
            assertEquals(topologyConfig.getAppServerUrl(i), "http://127.0.0.1:8080/v1");
        }
        for (int i = 8; i <= 15; ++i) {
            assertEquals(topologyConfig.getAppServerUrl(i), "http://127.0.0.1:8081/v1");
        }

        assertEquals(topologyConfig.getDataCenterUrl(0), "https://localhost:8080/v1");
        assertEquals(topologyConfig.getDataCenterUrl(1), "https://localhost:8180/v1");
        assertEquals(topologyConfig.getDataCenterUrlByName("dc0"), "https://localhost:8080/v1");
        assertEquals(topologyConfig.getDataCenterUrlByName("dc1"), "https://localhost:8180/v1");

        // handledShards should be 8..15
        int[] handledShards = topologyConfig.handledShards();
        assertEquals(handledShards.length, 8);
        for (int i = 0; i < handledShards.length; ++i) {
             assertEquals(handledShards[i], i + 8);
        }

        for (int i = 0; i <= 7; ++i) {
            assertTrue(topologyConfig.isHandledBy(i, "127.0.0.1", 8080));
            assertFalse(topologyConfig.isHandledBy(i, "127.0.0.1", 8081));
            assertFalse(topologyConfig.isHandledBySelf(i));
        }
        for (int i = 8; i <= 15; ++i) {
            assertFalse(topologyConfig.isHandledBy(i, "127.0.0.1", 8080));
            assertTrue(topologyConfig.isHandledBy(i, "127.0.0.1", 8081));
        }

        assertTrue(topologyConfig.isLocalDatacenter(0));
        assertFalse(topologyConfig.isLocalDatacenter(1));
        assertTrue(topologyConfig.isLocalDatacenter("dc0"));
        assertFalse(topologyConfig.isLocalDatacenter("dc1"));
    }

    //@Test
    public void testTopologyConfigMultipleAppServers() throws Exception {

        ConfigService configService = new ConfigService() {
            private ConfigContext context = new ConfigContext("mdctest", "0", "0.0.0.0/0");

            @Override
            public ConfigContext getConfigContext() {
                return context;
            }

            @Override
            public String getConfigValue(String configKey) {
                return null;
            }

            @Override
            public Properties getAllConfigItems() {
                return new Properties();
            }

            @Override
            public void addListener(String configKey, ConfigListener listener) {

            }
        };

        TopologyConfig topologyConfig = new TopologyConfig(
                "http://0.0.0.0:8081/v1",               // assume I am second app server
                "  https://localhost:8080/v1;0;dc0,\n" +
                "  https://localhost:8180/v1;1;dc1",
                "http://%s/v1",
                "  127.0.0.1:8080;0..3;dc0,\n" +
                "  127.0.0.1:8080;4..7;dc0,\n" +        // test merge
                "  127.0.0.1:8081;8..15;dc0,\n" +
                "  127.0.0.1:8082;0..3;dc0,\n" +
                "  127.0.0.1:8180;0..7;dc1,\n" +
                "  127.0.0.1:8181;8..15;dc1",
                configService
        );

        for (int i = 0; i <= 3; ++i) {
            while (!topologyConfig.getAppServerUrl(i).equals("http://127.0.0.1:8080/v1")) {
                ;
            }
            while (!topologyConfig.getAppServerUrl(i).equals("http://127.0.0.1:8082/v1")) {
                ;
            }
        }
    }
}
