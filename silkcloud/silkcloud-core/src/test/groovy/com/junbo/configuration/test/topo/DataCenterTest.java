/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.configuration.test.topo;

import com.junbo.configuration.test.TestConfigService;
import com.junbo.configuration.topo.DataCenters;
import com.junbo.configuration.topo.model.DataCentersConfig;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;

/**
 * Java doc for TopologyTest.
 */
public class DataCenterTest {
    @Test
    public void testDataCentersConfig() throws Exception {
        DataCentersConfig dataCentersConfig = new DataCentersConfig(
            "  https://localhost:8080/v1;0;dc0;2,\n" +
            "  https://localhost:8180/v1;1;dc1;2"
        );

        assertEquals(dataCentersConfig.getDataCenterUrl(0), "https://localhost:8080/v1");
        assertEquals(dataCentersConfig.getDataCenterUrl(1), "https://localhost:8180/v1");
        assertEquals(dataCentersConfig.getDataCenterUrl("dc0"), "https://localhost:8080/v1");
        assertEquals(dataCentersConfig.getDataCenterUrl("dc1"), "https://localhost:8180/v1");

        assertEquals(dataCentersConfig.getDataCenter(0), dataCentersConfig.getDataCenter("dc0"));
        assertEquals(dataCentersConfig.getDataCenter(1), dataCentersConfig.getDataCenter("dc1"));
    }

    @Test
    public void testDataCenters() throws Exception {
        try (TestConfigService configService = new TestConfigService() {
            @Override
            public void addListener(String configKey, ConfigListener listener) {
                assertEquals(configKey, "common.topo.datacenters");
                super.addListener(configKey, listener);
            }
        }) {
            configService.getAllConfigItems().put("common.topo.datacenters",
                    "  https://localhost:8080/v1;0;dc0;2,\n" +
                            "https://localhost:8180/v1;1;dc1;2"
            );

            DataCenters dcs = new DataCenters();
            assertEquals(dcs.getDataCenter(0).getName(), "dc0");
            assertEquals(dcs.getDataCenter(1).getName(), "dc1");

            assertTrue(dcs.hasDataCenter(0));
            assertTrue(dcs.hasDataCenter(1));
            assertFalse(dcs.hasDataCenter(2));

            assertTrue(dcs.hasDataCenter("dc0"));
            assertTrue(dcs.hasDataCenter("dc1"));
            assertFalse(dcs.hasDataCenter("dc2"));

            assertTrue(dcs.getDataCenterIds().size() == 2);

            assertTrue(dcs.isLocalDataCenter(0));
            assertFalse(dcs.isLocalDataCenter(1));
            assertTrue(dcs.isLocalDataCenter("dc0"));
            assertFalse(dcs.isLocalDataCenter("dc1"));

            assertEquals(dcs.getDataCenterUrl(0), "https://localhost:8080/v1");
            assertEquals(dcs.getDataCenterUrl(1), "https://localhost:8180/v1");
            assertEquals(dcs.getDataCenterUrl("dc0"), "https://localhost:8080/v1");
            assertEquals(dcs.getDataCenterUrl("dc1"), "https://localhost:8180/v1");

            // make sure no exception for non-existing datacenters
            assertFalse(dcs.isLocalDataCenter(-2));
            assertFalse(dcs.isLocalDataCenter("not_exists"));

            configService.updateConfig("common.topo.datacenters",
                    "  https://localhost:8080/v1;0;dc0;2,\n" +
                            "  https://localhost:8180/v1;1;dc11;2,\n" +
                            "  https://localhost:8280/v1;2;dc22;2"
            );

            assertEquals(dcs.getDataCenter(0).getName(), "dc0");
            assertEquals(dcs.getDataCenter(1).getName(), "dc11");
            assertEquals(dcs.getDataCenter(2).getName(), "dc22");

            assertTrue(dcs.hasDataCenter(0));
            assertTrue(dcs.hasDataCenter(1));
            assertTrue(dcs.hasDataCenter(2));
            assertFalse(dcs.hasDataCenter(3));

            assertTrue(dcs.hasDataCenter("dc0"));
            assertTrue(dcs.hasDataCenter("dc11"));
            assertTrue(dcs.hasDataCenter("dc22"));
            assertFalse(dcs.hasDataCenter("dc1"));
            assertFalse(dcs.hasDataCenter("dc2"));
            assertFalse(dcs.hasDataCenter("dc33"));

            assertTrue(dcs.isLocalDataCenter(0));
            assertFalse(dcs.isLocalDataCenter(1));
            assertFalse(dcs.isLocalDataCenter(2));
            assertFalse(dcs.isLocalDataCenter(3));
            assertTrue(dcs.isLocalDataCenter("dc0"));
            assertFalse(dcs.isLocalDataCenter("dc1"));
            assertFalse(dcs.isLocalDataCenter("dc11"));
            assertFalse(dcs.isLocalDataCenter("dc22"));
            assertFalse(dcs.isLocalDataCenter("dc33"));

            assertEquals(dcs.getDataCenterUrl(0), "https://localhost:8080/v1");
            assertEquals(dcs.getDataCenterUrl(1), "https://localhost:8180/v1");
            assertEquals(dcs.getDataCenterUrl(2), "https://localhost:8280/v1");
            assertEquals(dcs.getDataCenterUrl("dc0"), "https://localhost:8080/v1");
            assertEquals(dcs.getDataCenterUrl("dc11"), "https://localhost:8180/v1");
            assertEquals(dcs.getDataCenterUrl("dc22"), "https://localhost:8280/v1");
        }
    }
}
