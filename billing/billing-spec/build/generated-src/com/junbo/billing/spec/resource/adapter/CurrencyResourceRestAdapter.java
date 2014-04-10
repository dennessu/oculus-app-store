// CHECKSTYLE:OFF
package com.junbo.billing.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/currencies")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class CurrencyResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultCurrencyResource")
    private com.junbo.billing.spec.resource.CurrencyResource __adaptee;

    public com.junbo.billing.spec.resource.CurrencyResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.billing.spec.resource.CurrencyResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getCurrencies(@javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.billing.spec.model.Currency>> future;
    
        try {
            future = __adaptee.getCurrencies(
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.billing.spec.model.Currency>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.billing.spec.model.Currency> result) {
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
    @javax.ws.rs.Path("/{name}")
    public void getCurrency(
    @javax.ws.rs.PathParam("name") java.lang.String name,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.Currency> future;
    
        try {
            future = __adaptee.getCurrency(
                name
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.Currency>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.Currency result) {
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
