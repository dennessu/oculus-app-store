package com.junbo.apphost.core.health

import com.junbo.configuration.ConfigService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

/**
 * Created by kgu on 6/2/14.
 */
@Path("/properties")
@Produces(["application/json"])
@CompileStatic
class PropertiesEndpoint {

    @Autowired
    private ConfigService configService

    @GET
    public Map getProperties() {
        return new TreeMap(configService.allConfigItemsMasked)
    }
}
