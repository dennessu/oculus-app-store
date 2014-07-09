package com.junbo.langur.core.adapter
import com.google.common.base.Function
import com.junbo.langur.core.IpUtil
import com.junbo.langur.core.context.JunboHttpContext
import groovy.transform.CompileStatic
import org.glassfish.grizzly.http.server.Request
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

    @Context
    private Request grizzlyRequest

    JunboHttpContext.JunboHttpContextData __createJunboHttpContextData(Class requestHandler) {

        def httpContextData = new JunboHttpContext.JunboHttpContextData(
                requestMethod: httpRequestContext.method,
                requestUri: httpRequestContext.uriInfo.absolutePath,
                requestHandler: requestHandler
        )

        for (Map.Entry<String, List<String>> entry : httpRequestContext.headers.entrySet()) {
            httpContextData.requestHeaders.addAll(entry.key, entry.value)
        }

        if (grizzlyRequest != null) {
            httpContextData.requestIpAddress = IpUtil.getClientIpFromRequest(grizzlyRequest)
        }

        return httpContextData
    }

    void __processResponseData() {

        respondingContext.push(new Function<ContainerResponse, ContainerResponse>() {
            @Override
            ContainerResponse apply(ContainerResponse containerResponse) {

                if (JunboHttpContext.responseStatus != -1) {
                    containerResponse.status = JunboHttpContext.responseStatus
                }

                if (JunboHttpContext.responseHeaders != null) {
                    for (Map.Entry<String, List<String>> entry : JunboHttpContext.responseHeaders.entrySet()) {
                        for (String value : entry.value) {
                            containerResponse.headers.add(entry.key, value)
                        }
                    }
                }

                return containerResponse
            }
        })
    }
}
