package com.goshop.catalog.clientproxy;

import com.google.common.util.concurrent.ListenableFuture;
import com.goshop.langur.core.RestResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("myresource")
@RestResource
public interface
        MyResource {

    @GET
    @Produces(MediaType.TEXT_HTML)
    ListenableFuture<String> getIt();
}
