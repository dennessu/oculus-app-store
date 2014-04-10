// CHECKSTYLE:OFF
package com.junbo.subscription.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/subscriptions")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class SubscriptionResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultSubscriptionResource")
    private com.junbo.subscription.spec.resource.SubscriptionResource __adaptee;

    public com.junbo.subscription.spec.resource.SubscriptionResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.subscription.spec.resource.SubscriptionResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/")
    public void postSubscription(
    @javax.validation.Valid com.junbo.subscription.spec.model.Subscription request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.subscription.spec.model.Subscription> future;
    
        try {
            future = __adaptee.postSubscription(
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.subscription.spec.model.Subscription>() {
            @Override
            public void invoke(com.junbo.subscription.spec.model.Subscription result) {
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
    @javax.ws.rs.Path("/{subscriptionId}")
    public void getSubscription(
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders httpHeaders,
    
    @javax.ws.rs.PathParam("subscriptionId") com.junbo.common.id.SubscriptionId subscriptionId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.subscription.spec.model.Subscription> future;
    
        try {
            future = __adaptee.getSubscription(
                httpHeaders,
                subscriptionId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.subscription.spec.model.Subscription>() {
            @Override
            public void invoke(com.junbo.subscription.spec.model.Subscription result) {
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
