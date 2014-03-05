package com.goshop.catalog.spec.resource;

import com.goshop.catalog.spec.model.ResultList;
import com.goshop.catalog.spec.model.options.GetOptions;
import com.goshop.catalog.spec.model.options.product.ProductGetOptions;
import com.goshop.catalog.spec.model.product.Product;
import com.goshop.catalog.spec.model.product.ProductHist;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("products")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface ProductResource {

    @GET
    public ResultList<Product> getProducts(@BeanParam ProductGetOptions getOptions);

    @GET
    @Path("/{productId}")
    public Product getProduct(@PathParam("productId") String productId, @BeanParam GetOptions getOptions);

    @GET
    @Path("/{productId}/draft")
    public Product getProductDraft(@PathParam("productId") String productId, @BeanParam GetOptions getOptions);

    @GET
    @Path("/{productId}/hists")
    public ResultList<Product> getProductHists(@PathParam("productId") String productId, @BeanParam GetOptions getOptions);

    @GET
    @Path("/{productId}/hists/{version}")
    public Product getProductRevision(@PathParam("productId") String productId, @PathParam("version") Long version, @BeanParam GetOptions getOptions);

    // Write API
    @POST
    public Product createProduct(Product product);

    @POST
    @Path("/{productId}")
    public Product updateProduct(@PathParam("productId") String productId, Product product);

    @POST
    @Path("/{productId}/release")
    public Product releaseProduct(@PathParam("productId") String productId);

    @POST
    @Path("/{productId}/delete")
    public Product deleteProduct(@PathParam("productId") String productId);
}
