// CHECKSTYLE:OFF
package com.junbo.fulfilment.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/fulfilments")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class FulfilmentResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultFulfilmentResource")
    private com.junbo.fulfilment.spec.resource.FulfilmentResource __adaptee;

    public com.junbo.fulfilment.spec.resource.FulfilmentResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.fulfilment.spec.resource.FulfilmentResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/")
    public void fulfill(
    @javax.validation.Valid com.junbo.fulfilment.spec.model.FulfilmentRequest request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.fulfilment.spec.model.FulfilmentRequest> future;
    
        try {
            future = __adaptee.fulfill(
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.fulfilment.spec.model.FulfilmentRequest>() {
            @Override
            public void invoke(com.junbo.fulfilment.spec.model.FulfilmentRequest result) {
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
    @javax.ws.rs.Path("/")
    public void getByOrderId(
    @javax.ws.rs.QueryParam("orderId") com.junbo.common.id.OrderId orderId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.fulfilment.spec.model.FulfilmentRequest> future;
    
        try {
            future = __adaptee.getByOrderId(
                orderId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.fulfilment.spec.model.FulfilmentRequest>() {
            @Override
            public void invoke(com.junbo.fulfilment.spec.model.FulfilmentRequest result) {
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
    @javax.ws.rs.Path("/{fulfilmentId}")
    public void getByFulfilmentId(
    @javax.ws.rs.PathParam("fulfilmentId") com.junbo.common.id.FulfilmentId fulfilmentId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.fulfilment.spec.model.FulfilmentItem> future;
    
        try {
            future = __adaptee.getByFulfilmentId(
                fulfilmentId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.fulfilment.spec.model.FulfilmentItem>() {
            @Override
            public void invoke(com.junbo.fulfilment.spec.model.FulfilmentItem result) {
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
