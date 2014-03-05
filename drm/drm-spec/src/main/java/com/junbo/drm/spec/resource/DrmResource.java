package com.junbo.drm.spec.resource;

import com.google.common.util.concurrent.ListenableFuture;
import com.junbo.langur.core.RestResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
//@RestResource
public interface DrmResource {

}
