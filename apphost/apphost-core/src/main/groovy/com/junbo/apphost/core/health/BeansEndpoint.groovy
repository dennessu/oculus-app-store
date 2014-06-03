package com.junbo.apphost.core.health

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.support.LiveBeansView

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
 * Created by kgu on 6/3/14.
 */
@Path("/beans")
@Produces(["application/json"])
@CompileStatic
class BeansEndpoint {

    @Autowired
    private ApplicationContext applicationContext

    @GET
    List<Object> getBeans() {
        def view = new LiveBeansView()
        view.setApplicationContext(applicationContext)

        return new ObjectMapper().readValue(view.getSnapshotAsJson(), List.class)
    }
}
