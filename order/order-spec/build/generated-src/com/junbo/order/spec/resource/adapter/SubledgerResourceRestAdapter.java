// CHECKSTYLE:OFF
package com.junbo.order.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("subledgers")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class SubledgerResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultSubledgerResource")
    private com.junbo.order.spec.resource.SubledgerResource __adaptee;

    public com.junbo.order.spec.resource.SubledgerResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.order.spec.resource.SubledgerResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getSubledgers(
    @javax.ws.rs.QueryParam("sellerId") java.lang.String sellerId,
    
    @javax.ws.rs.QueryParam("status") java.lang.String status,
    
    @javax.ws.rs.QueryParam("fromDate") java.util.Date fromDate,
    
    @javax.ws.rs.QueryParam("toDate") java.util.Date toDate,
    
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders headers,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.order.spec.model.Subledger>> future;
    
        try {
            future = __adaptee.getSubledgers(
                sellerId,
                status,
                fromDate,
                toDate,
                headers
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.order.spec.model.Subledger>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.order.spec.model.Subledger> result) {
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
