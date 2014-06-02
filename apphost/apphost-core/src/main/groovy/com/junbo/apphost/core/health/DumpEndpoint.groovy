package com.junbo.apphost.core.health

import groovy.transform.CompileStatic

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import java.lang.management.ManagementFactory
import java.lang.management.ThreadInfo

/**
 * Created by kgu on 6/3/14.
 */
@Path("/dump")
@Produces(["application/json"])
@CompileStatic
class DumpEndpoint {

    @GET
    public List<ThreadInfo> dump() {
        return Arrays.asList(ManagementFactory.threadMXBean.dumpAllThreads(true, true))
    }
}
