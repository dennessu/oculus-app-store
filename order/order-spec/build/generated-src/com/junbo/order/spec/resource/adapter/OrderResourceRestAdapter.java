// CHECKSTYLE:OFF
package com.junbo.order.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("orders")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class OrderResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultOrderResource")
    private com.junbo.order.spec.resource.OrderResource __adaptee;

    public com.junbo.order.spec.resource.OrderResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.order.spec.resource.OrderResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void createOrder(
    com.junbo.order.spec.model.Order order,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.order.spec.model.Order> future;
    
        try {
            future = __adaptee.createOrder(
                order
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.order.spec.model.Order>() {
            @Override
            public void invoke(com.junbo.order.spec.model.Order result) {
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
    @javax.ws.rs.Path("/{orderId}")
    public void getOrderByOrderId(
    @javax.ws.rs.PathParam("orderId") com.junbo.common.id.OrderId orderId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.order.spec.model.Order> future;
    
        try {
            future = __adaptee.getOrderByOrderId(
                orderId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.order.spec.model.Order>() {
            @Override
            public void invoke(com.junbo.order.spec.model.Order result) {
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
    public void getOrderByUserId(
    @javax.ws.rs.QueryParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.BeanParam com.junbo.order.spec.model.OrderQueryParam orderQueryParam,
    
    @javax.ws.rs.BeanParam com.junbo.order.spec.model.PageParam pageParam,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.order.spec.model.Order>> future;
    
        try {
            future = __adaptee.getOrderByUserId(
                userId,
                orderQueryParam,
                pageParam
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.order.spec.model.Order>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.order.spec.model.Order> result) {
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
    
    @javax.ws.rs.PUT
    @javax.ws.rs.Path("/{orderId}")
    public void updateOrderByOrderId(
    @javax.ws.rs.PathParam("orderId") com.junbo.common.id.OrderId orderId,
    
    com.junbo.order.spec.model.Order order,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.order.spec.model.Order> future;
    
        try {
            future = __adaptee.updateOrderByOrderId(
                orderId,
                order
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.order.spec.model.Order>() {
            @Override
            public void invoke(com.junbo.order.spec.model.Order result) {
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
