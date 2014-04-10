// CHECKSTYLE:OFF
package com.junbo.order.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("subledger-items")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class SubledgerItemResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultSubledgerItemResource")
    private com.junbo.order.spec.resource.SubledgerItemResource __adaptee;

    public com.junbo.order.spec.resource.SubledgerItemResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.order.spec.resource.SubledgerItemResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void createSubledgerItem(
    com.junbo.order.spec.model.SubledgerItem subledgerItem,
    
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders headers,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.order.spec.model.SubledgerItem> future;
    
        try {
            future = __adaptee.createSubledgerItem(
                subledgerItem,
                headers
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.order.spec.model.SubledgerItem>() {
            @Override
            public void invoke(com.junbo.order.spec.model.SubledgerItem result) {
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
