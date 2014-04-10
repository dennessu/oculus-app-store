// CHECKSTYLE:OFF
package com.junbo.order.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("order-events")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class OrderEventResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultOrderEventResource")
    private com.junbo.order.spec.resource.OrderEventResource __adaptee;

    public com.junbo.order.spec.resource.OrderEventResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.order.spec.resource.OrderEventResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getOrderEvents(
    @javax.ws.rs.QueryParam("orderId") com.junbo.common.id.OrderId orderId,
    
    @javax.ws.rs.BeanParam com.junbo.order.spec.model.PageParam pageParam,
    
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders headers,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.order.spec.model.OrderEvent>> future;
    
        try {
            future = __adaptee.getOrderEvents(
                orderId,
                pageParam,
                headers
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.order.spec.model.OrderEvent>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.order.spec.model.OrderEvent> result) {
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
    
    @javax.ws.rs.POST
    public void createOrderEvent(
    com.junbo.order.spec.model.OrderEvent orderEvent,
    
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders headers,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.order.spec.model.OrderEvent> future;
    
        try {
            future = __adaptee.createOrderEvent(
                orderEvent,
                headers
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.order.spec.model.OrderEvent>() {
            @Override
            public void invoke(com.junbo.order.spec.model.OrderEvent result) {
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
