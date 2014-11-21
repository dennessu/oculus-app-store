package com.junbo.apphost.core.health

import com.junbo.apphost.core.JunboApplication
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

import javax.ws.rs.*
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.Response

/**
 * Created by kgu on 6/3/14.
 */
@Path("/health")
@Consumes(["application/json"])
@Produces(["application/json"])
@CompileStatic
class HealthEndpoint {
    public static Boolean serviceOnline = true

    @Autowired
    private ApplicationContext applicationContext

    @Autowired
    private HealthService healthService

    @GET
    public Response getHealth() {
        def junboApplicationContext = (JunboApplication.JunboApplicationContext) applicationContext

        if (junboApplicationContext.isRefreshed) {
            if (serviceOnline) {
                return Response.ok([status: "ok"]).build()
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/check")
    public void getHealthCheck(@Suspended final AsyncResponse asyncResponse) {
        def junboApplicationContext = (JunboApplication.JunboApplicationContext) applicationContext

        try {
            if (junboApplicationContext.isRefreshed) {
                if (serviceOnline) {
                    healthService.testHealth().syncThen { Response result ->
                        asyncResponse.resume(result);
                    }.syncRecover { Throwable ex ->
                        asyncResponse.resume(ex);
                    }
                } else {
                    asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build());
                }
            } else {
                asyncResponse.resume(Response.status(Response.Status.NOT_FOUND).build());
            }
        } catch (Throwable ex) {
            asyncResponse.resume(ex);
        }
    }
}
