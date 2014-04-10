// CHECKSTYLE:OFF
package com.junbo.ewallet.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/wallets")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class WalletResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultWalletResource")
    private com.junbo.ewallet.spec.resource.WalletResource __adaptee;

    public com.junbo.ewallet.spec.resource.WalletResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.ewallet.spec.resource.WalletResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{walletId}")
    public void getWallet(
    @javax.ws.rs.PathParam("walletId") com.junbo.common.id.WalletId walletId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.ewallet.spec.model.Wallet> future;
    
        try {
            future = __adaptee.getWallet(
                walletId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.ewallet.spec.model.Wallet>() {
            @Override
            public void invoke(com.junbo.ewallet.spec.model.Wallet result) {
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
    public void postWallet(
    com.junbo.ewallet.spec.model.Wallet wallet,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.ewallet.spec.model.Wallet> future;
    
        try {
            future = __adaptee.postWallet(
                wallet
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.ewallet.spec.model.Wallet>() {
            @Override
            public void invoke(com.junbo.ewallet.spec.model.Wallet result) {
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
    @javax.ws.rs.Path("/{walletId}")
    public void updateWallet(
    @javax.ws.rs.PathParam("walletId") com.junbo.common.id.WalletId walletId,
    
    com.junbo.ewallet.spec.model.Wallet wallet,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.ewallet.spec.model.Wallet> future;
    
        try {
            future = __adaptee.updateWallet(
                walletId,
                wallet
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.ewallet.spec.model.Wallet>() {
            @Override
            public void invoke(com.junbo.ewallet.spec.model.Wallet result) {
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
    @javax.ws.rs.Path("/{walletId}/credit")
    public void credit(
    @javax.ws.rs.PathParam("walletId") com.junbo.common.id.WalletId walletId,
    
    com.junbo.ewallet.spec.model.CreditRequest creditRequest,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.ewallet.spec.model.Wallet> future;
    
        try {
            future = __adaptee.credit(
                walletId,
                creditRequest
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.ewallet.spec.model.Wallet>() {
            @Override
            public void invoke(com.junbo.ewallet.spec.model.Wallet result) {
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
    @javax.ws.rs.Path("/{walletId}/debit")
    public void debit(
    @javax.ws.rs.PathParam("walletId") com.junbo.common.id.WalletId walletId,
    
    com.junbo.ewallet.spec.model.DebitRequest debitRequest,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.ewallet.spec.model.Wallet> future;
    
        try {
            future = __adaptee.debit(
                walletId,
                debitRequest
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.ewallet.spec.model.Wallet>() {
            @Override
            public void invoke(com.junbo.ewallet.spec.model.Wallet result) {
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
    @javax.ws.rs.Path("/{walletId}/transactions")
    public void getTransactions(
    @javax.ws.rs.PathParam("walletId") com.junbo.common.id.WalletId walletId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.ewallet.spec.model.Wallet> future;
    
        try {
            future = __adaptee.getTransactions(
                walletId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.ewallet.spec.model.Wallet>() {
            @Override
            public void invoke(com.junbo.ewallet.spec.model.Wallet result) {
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
