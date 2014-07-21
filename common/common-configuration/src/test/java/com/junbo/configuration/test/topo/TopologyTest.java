/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.test.topo;

import com.junbo.configuration.test.TestConfigService;
import com.junbo.configuration.topo.model.TopologyConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Java doc for TopologyTest.
 */
public class TopologyTest {
    @Test
    public void testTopologyConfig() throws Exception {
        try (TestConfigService configService = new TestConfigService()) {

            TopologyConfig topologyConfig = new TopologyConfig(
                    "http://0.0.0.0:8081/v1",               // assume I am second app server
                    "http://%s/v1",
                    "  127.0.0.1:8080;0..3;dc0,\n" +
                    "  127.0.0.1:8080;4..7;dc0,\n" +        // test merge
                    "  127.0.0.1:8081;8..15;dc0,\n" +
                    "  127.0.0.1:8180;0..7;dc1,\n" +
                    "  127.0.0.1:8181;8..15;dc1",
                    "  10.0.0.10,\n" +
                    "  10.0.0.20,\n",
                    configService
            );

            for (int i = 0; i <= 7; ++i) {
                assertEquals(topologyConfig.getAppServerUrl(i), "http://127.0.0.1:8080/v1");
            }
            for (int i = 8; i <= 15; ++i) {
                assertEquals(topologyConfig.getAppServerUrl(i), "http://127.0.0.1:8081/v1");
            }

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

            assertTrue(topologyConfig.isOtherServer("10.0.0.10"));
            assertTrue(topologyConfig.isOtherServer("10.0.0.20"));
        }
    }

    @Test
    public void testTopologyConfigMultipleAppServers() throws Exception {
        try (TestConfigService configService = new TestConfigService()) {
            TopologyConfig topologyConfig = new TopologyConfig(
                    "http://0.0.0.0:8081/v1",               // assume I am second app server
                    "http://%s/v1",
                    "  127.0.0.1:8080;0..3;dc0,\n" +
                    "  127.0.0.1:8080;4..7;dc0,\n" +        // test merge
                    "  127.0.0.1:8081;8..15;dc0,\n" +
                    "  127.0.0.1:8082;0..3;dc0,\n" +
                    "  127.0.0.1:8180;0..7;dc1,\n" +
                    "  127.0.0.1:8181;8..15;dc1",
                    "",
                    configService
            );

            for (int i = 0; i <= 3; ++i) {
                while (!topologyConfig.getAppServerUrl(i).equals("http://127.0.0.1:8080/v1")) {
                    // eventually will hit
                }
                while (!topologyConfig.getAppServerUrl(i).equals("http://127.0.0.1:8082/v1")) {
                    // eventually will hit
                }
            }
        }
    }
}
