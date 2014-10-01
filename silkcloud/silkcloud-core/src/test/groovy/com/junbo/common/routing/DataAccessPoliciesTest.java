/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.routing;

import com.junbo.common.routing.model.DataAccessAction;
import com.junbo.common.routing.model.DataAccessPolicy;
import com.junbo.common.routing.model.DataAccessPolicyConfigs;
import org.testng.annotations.Test;

import javax.ws.rs.HttpMethod;

import static org.testng.Assert.assertEquals;

/**
 * Java doc for TopologyTest.
 */
public class DataAccessPoliciesTest {
    @Test
    public void testDataAccessPolicies() throws Exception {
        DataAccessPolicyConfigs data = new DataAccessPolicyConfigs(
            "  READ : com.junbo.common.routing.model : CLOUDANT_ONLY,\n" +
            "  READ : com.junbo.common.routing.model.DataAccessPolicy : SQL_FIRST,\n" +
            "  WRITE : com.junbo.common.routing.model : SQL_ONLY,\n",
            // action map
            "  GET : com.junbo.common.routing.model : WRITE,\n" +
            "  GET : com.junbo.common.routing.model.DataAccessPolicy : READ,\n" +
            "  POST : com.junbo.common.routing.model.DataAccessPolicy : READ"
        );

        assertEquals(data.getDataAccessPolicy(DataAccessAction.READ,  DataAccessAction.class), DataAccessPolicy.CLOUDANT_ONLY);
        assertEquals(data.getDataAccessPolicy(DataAccessAction.READ,  DataAccessPolicy.class), DataAccessPolicy.SQL_FIRST);
        assertEquals(data.getDataAccessPolicy(DataAccessAction.WRITE, DataAccessPolicy.class), DataAccessPolicy.SQL_ONLY);

        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.GET,     DataAccessAction.class), DataAccessPolicy.SQL_ONLY);
        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.POST,    DataAccessAction.class), DataAccessPolicy.SQL_ONLY);
        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.PUT,     DataAccessAction.class), DataAccessPolicy.SQL_ONLY);
        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.DELETE,  DataAccessAction.class), DataAccessPolicy.SQL_ONLY);

        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.GET,     DataAccessPolicy.class), DataAccessPolicy.SQL_FIRST);
        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.POST,    DataAccessPolicy.class), DataAccessPolicy.SQL_FIRST);
        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.PUT,     DataAccessPolicy.class), DataAccessPolicy.SQL_ONLY);
        assertEquals(data.getHttpDataAccessPolicy(HttpMethod.DELETE,  DataAccessPolicy.class), DataAccessPolicy.SQL_ONLY);
    }
}
