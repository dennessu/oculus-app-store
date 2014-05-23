package com.junbo.langur.core.adapter

import com.google.common.base.Function
import com.google.common.collect.HashMultimap
import com.junbo.langur.core.context.JunboHttpContext
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.ContainerResponse
import org.glassfish.jersey.server.internal.process.RespondingContext

import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.Context

/**
 * Created by kg on 5/23/2014.
 */
@CompileStatic
abstract class AbstractRestAdapter {

    @Context
    private ContainerRequestContext httpRequestContext

    @Context
    private RespondingContext respondingContext

    JunboHttpContext.JunboHttpContextData __createJunboHttpContextData() {

        def httpContextData = new JunboHttpContext.JunboHttpContextData(
                requestMethod: httpRequestContext.method,
                requestUri: httpRequestContext.uriInfo.absolutePath,
                requestHeaders: HashMultimap.create(),
                responseStatus: -1,
                responseHeaders: HashMultimap.create()
        )

        for (def entry : httpRequestContext.getHeaders().entrySet()) {
            httpContextData.requestHeaders.putAll(entry.key, entry.value)
        }

        return httpContextData
    }

    void __processResponseData() {

        respondingContext.push(new Function<ContainerResponse, ContainerResponse>() {
            @Override
            ContainerResponse apply(ContainerResponse containerResponse) {

                if (JunboHttpContext.responseStatus != -1) {
                    containerResponse.status = JunboHttpContext.responseStatus

                    for (def entry : JunboHttpContext.responseHeaders.entries()) {
                        containerResponse.headers.add(entry.key, entry.value)
                    }
                }

                return containerResponse
            }
        })
    }
}
