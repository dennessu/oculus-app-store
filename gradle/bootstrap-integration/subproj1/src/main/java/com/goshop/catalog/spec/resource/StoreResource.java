package com.goshop.catalog.spec.resource;

import com.goshop.catalog.spec.model.options.GetOptions;
import com.goshop.catalog.spec.model.store.Store;
import com.goshop.catalog.spec.model.store.StoreHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("stores")
@Produces({MediaType.APPLICATION_JSON})
public interface StoreResource {

    @GET
    public List<Store> getStores(@BeanParam GetOptions getOptions);

    @GET
    @Path("/{storeId}")
    public Store getStore(@PathParam("storeId") String storeId, @BeanParam GetOptions getOptions);

    @GET
    @Path("/{storeId}/draft")
    public Store getStoreDraft(@PathParam("storeId") String storeId, @BeanParam GetOptions getOptions);

    @POST
    public Store createStore(Store store);

    @PUT
    public Store updateStore(Store store);

    @POST
    @Path("/{storeId}")
    public Store patchStore(@PathParam("storeId") String storeId, Store store);

    @POST
    @Path("/{storeId}/release")
    public Store releaseStore(@PathParam("storeId") String storeId);

    @GET
    @Path("/{storeId}/hists")
    public List<StoreHist> getStoreHists(@PathParam("storeId") String storeId, @BeanParam GetOptions getOptions);

    @GET
    @Path("/{storeId}/hists/{version}")
    public StoreHist getStoreHist(@PathParam("storeId") String storeId, @PathParam("version") Long version, @BeanParam GetOptions getOptions);
}
