package com.junbo.apphost.core.health

import com.junbo.apphost.core.GrizzlyHttpServerBean
import groovy.transform.CompileStatic
import org.glassfish.jersey.server.internal.JerseyResourceContext
import org.glassfish.jersey.server.model.Resource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.UriInfo

/**
 * Created by kg on 6/3/2014.
 */
@Path("/resources")
@Produces(["application/json"])
@CompileStatic
class ResourcesEndpoint {

    @Autowired
    @Qualifier('httpServer')
    private GrizzlyHttpServerBean httpServer

    @Context
    private JerseyResourceContext jerseyResourceContext

    @Context
    private UriInfo uriInfo

    @GET
    public List<PathInfo> getResources() {
        def resourcePaths = new ArrayList<String>()

        for (String path : jerseyResourceContext.resourceModel.resources.collect { Resource resource ->
            String item = resource.path

            if (item.startsWith('/')) {
                item = item.substring(1, item.length())
            }
            return item
        }.sort()) {
            resourcePaths.add(uriInfo.baseUriBuilder.path(path).build().toString())
        }

        String baseUri = httpServer.uri
        if (baseUri.endsWith('/')) {
            baseUri.substring(0, baseUri.length() - 1)
        }

        def resourceContext = (JerseyResourceContext) httpServer.serviceLocator.getService(JerseyResourceContext)
        for (String path : resourceContext.resourceModel.resources.collect { Resource resource ->
            String item = resource.path

            if (item.startsWith('/')) {
                item = item.substring(1, item.length())
            }
            return item
        }.sort()) {
            resourcePaths.add(baseUri + '/' + path)
        }

        return resourcePaths.collect { String path ->
            new PathInfo(
                    path: path
            )
        }
    }

    @CompileStatic
    static class PathInfo {
        String path
    }
}