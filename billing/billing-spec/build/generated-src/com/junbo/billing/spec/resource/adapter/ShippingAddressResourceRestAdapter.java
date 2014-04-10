// CHECKSTYLE:OFF
package com.junbo.billing.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/users/{userId}/ship-to-info")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class ShippingAddressResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultShippingAddressResource")
    private com.junbo.billing.spec.resource.ShippingAddressResource __adaptee;

    public com.junbo.billing.spec.resource.ShippingAddressResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.billing.spec.resource.ShippingAddressResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void postShippingAddress(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    com.junbo.billing.spec.model.ShippingAddress address,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.ShippingAddress> future;
    
        try {
            future = __adaptee.postShippingAddress(
                userId,
                address
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.ShippingAddress>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.ShippingAddress result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    public void getShippingAddresses(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.billing.spec.model.ShippingAddress>> future;
    
        try {
            future = __adaptee.getShippingAddresses(
                userId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.billing.spec.model.ShippingAddress>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.billing.spec.model.ShippingAddress> result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{addressId}")
    public void getShippingAddress(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("addressId") com.junbo.common.id.ShippingAddressId addressId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.ShippingAddress> future;
    
        try {
            future = __adaptee.getShippingAddress(
                userId,
                addressId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.ShippingAddress>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.ShippingAddress result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
    
    @javax.ws.rs.DELETE
    @javax.ws.rs.Path("/{addressId}")
    public void deleteShippingAddress(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("addressId") com.junbo.common.id.ShippingAddressId addressId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.deleteShippingAddress(
                userId,
                addressId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<javax.ws.rs.core.Response>() {
            @Override
            public void invoke(javax.ws.rs.core.Response result) {
                __asyncResponse.resume(result);
            }
        });
    
        future.onFailure(new com.junbo.langur.core.promise.Promise.Callback<Throwable>() {
            @Override
            public void invoke(Throwable result) {
                __asyncResponse.resume(result);
            }
        });
    }
}
