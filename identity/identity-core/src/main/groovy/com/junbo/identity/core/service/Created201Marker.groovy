/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.core.service
import com.google.common.base.Function
import com.junbo.common.id.Id
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerResponse
import org.glassfish.jersey.server.internal.process.RespondingContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider
/**
 * Created by kg on 3/17/14.
 */
@Provider
@Component
@Scope('prototype')
@CompileStatic
class Created201Marker {

    @Autowired
    private ContainerRequestContext requestContext

    @Autowired
    private RespondingContext respondingContext

    void mark(Id resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException('resourceId is null')
        }

        // process response header
        if (requestContext != null && respondingContext != null) {
            def location = requestContext.uriInfo.absolutePath.toString() + '/' + resourceId.value

            respondingContext.push(
                    { ContainerResponse response ->

                        if (response.statusInfo.family == Response.Status.Family.SUCCESSFUL) {
                            response.headers.add('location', location)

                            if (response.statusInfo == Response.Status.OK) {
                                response.statusInfo = Response.Status.CREATED
                            }
                        }

                        return response

                    } as Function<ContainerResponse, ContainerResponse>)
        }
    }
}
