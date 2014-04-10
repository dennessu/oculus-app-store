// CHECKSTYLE:OFF
package com.junbo.payment.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/payment-transactions")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class PaymentTransactionResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultPaymentTransactionResource")
    private com.junbo.payment.spec.resource.PaymentTransactionResource __adaptee;

    public com.junbo.payment.spec.resource.PaymentTransactionResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.payment.spec.resource.PaymentTransactionResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/authorization")
    public void postPaymentAuthorization(
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.postPaymentAuthorization(
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.payment.spec.model.PaymentTransaction>() {
            @Override
            public void invoke(com.junbo.payment.spec.model.PaymentTransaction result) {
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
    @javax.ws.rs.Path("/{paymentId}/capture")
    public void postPaymentCapture(
    @javax.ws.rs.PathParam("paymentId") java.lang.Long paymentId,
    
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.postPaymentCapture(
                paymentId,
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.payment.spec.model.PaymentTransaction>() {
            @Override
            public void invoke(com.junbo.payment.spec.model.PaymentTransaction result) {
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
    @javax.ws.rs.Path("/charge")
    public void postPaymentCharge(
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.postPaymentCharge(
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.payment.spec.model.PaymentTransaction>() {
            @Override
            public void invoke(com.junbo.payment.spec.model.PaymentTransaction result) {
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
    @javax.ws.rs.Path("/{paymentId}/reverse")
    public void reversePayment(
    @javax.ws.rs.PathParam("paymentId") java.lang.Long paymentId,
    
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.reversePayment(
                paymentId,
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.payment.spec.model.PaymentTransaction>() {
            @Override
            public void invoke(com.junbo.payment.spec.model.PaymentTransaction result) {
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
    @javax.ws.rs.Path("/{paymentId}")
    public void getPayment(
    @javax.ws.rs.PathParam("paymentId") java.lang.Long paymentId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.getPayment(
                paymentId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.payment.spec.model.PaymentTransaction>() {
            @Override
            public void invoke(com.junbo.payment.spec.model.PaymentTransaction result) {
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
