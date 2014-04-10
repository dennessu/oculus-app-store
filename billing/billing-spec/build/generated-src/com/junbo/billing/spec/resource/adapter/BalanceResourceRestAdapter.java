// CHECKSTYLE:OFF
package com.junbo.billing.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/balances")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class BalanceResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultBalanceResource")
    private com.junbo.billing.spec.resource.BalanceResource __adaptee;

    public com.junbo.billing.spec.resource.BalanceResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.billing.spec.resource.BalanceResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void postBalance(
    com.junbo.billing.spec.model.Balance balance,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.Balance> future;
    
        try {
            future = __adaptee.postBalance(
                balance
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.Balance>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.Balance result) {
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
    @javax.ws.rs.Path("/quote")
    public void quoteBalance(
    com.junbo.billing.spec.model.Balance balance,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.Balance> future;
    
        try {
            future = __adaptee.quoteBalance(
                balance
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.Balance>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.Balance result) {
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
    @javax.ws.rs.Path("/capture")
    public void captureBalance(
    com.junbo.billing.spec.model.Balance balance,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.Balance> future;
    
        try {
            future = __adaptee.captureBalance(
                balance
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.Balance>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.Balance result) {
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
    @javax.ws.rs.Path("/process-async")
    public void processAsyncBalance(
    com.junbo.billing.spec.model.Balance balance,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.Balance> future;
    
        try {
            future = __adaptee.processAsyncBalance(
                balance
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.Balance>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.Balance result) {
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
    @javax.ws.rs.Path("/{balanceId}")
    public void getBalance(
    @javax.ws.rs.PathParam("balanceId") com.junbo.common.id.BalanceId balanceId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.billing.spec.model.Balance> future;
    
        try {
            future = __adaptee.getBalance(
                balanceId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.billing.spec.model.Balance>() {
            @Override
            public void invoke(com.junbo.billing.spec.model.Balance result) {
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
    public void getBalances(
    @javax.ws.rs.QueryParam("orderId") com.junbo.common.id.OrderId orderId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.billing.spec.model.Balance>> future;
    
        try {
            future = __adaptee.getBalances(
                orderId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.billing.spec.model.Balance>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.billing.spec.model.Balance> result) {
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
