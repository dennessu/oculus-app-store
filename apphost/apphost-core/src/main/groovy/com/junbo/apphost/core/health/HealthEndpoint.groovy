package com.junbo.apphost.core.health

import com.junbo.apphost.core.JunboApplication
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Response

/**
 * Created by kgu on 6/3/14.
 */
@Path("/health")
@Produces(["application/json"])
@CompileStatic
class HealthEndpoint {
    public static Boolean serviceOnline = true

    @Autowired
    private ApplicationContext applicationContext

    @GET
    public Response getHealth() {
        def junboApplicationContext = (JunboApplication.JunboApplicationContext) applicationContext

        if (junboApplicationContext.isRefreshed) {
            if (serviceOnline) {
                return Response.ok([status: "ok"]).build()
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE).build()
            }
        }

        return Response.status(Response.Status.NOT_FOUND).build()
    }
}
