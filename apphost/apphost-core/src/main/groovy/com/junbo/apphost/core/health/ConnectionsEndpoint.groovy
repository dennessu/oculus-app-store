/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.apphost.core.health

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
 * ConnectionsEndpoint.
 */
@Path("/connections")
@Produces(["application/json"])
@CompileStatic
class ConnectionsEndpoint {
    @Autowired
    private ApplicationContext applicationContext

    private Map<String, ConnectionInfoProvider> connectionInfoProviders

    @GET
    Map getConnectionsInfo() {
        if (connectionInfoProviders == null) {
            synchronized (this) {
                this.connectionInfoProviders = applicationContext.getBeansOfType(ConnectionInfoProvider)
            }
        }

        Map<String, Map<String, String>> result = [:] as Map
        for (Map.Entry<String, ConnectionInfoProvider> entry : connectionInfoProviders.entrySet()) {
            result[entry.key] = entry.value.connectionInfo
        }

        return result
    }
}
