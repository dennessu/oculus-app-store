// CHECKSTYLE:OFF
package com.junbo.payment.spec.internal.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/braintree")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class BrainTreeResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultBrainTreeResource")
    private com.junbo.payment.spec.internal.BrainTreeResource __adaptee;

    public com.junbo.payment.spec.internal.BrainTreeResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.payment.spec.internal.BrainTreeResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/payment-instruments")
    public void addPaymentInstrument(
    com.junbo.payment.spec.model.PaymentInstrument request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentInstrument> future;
    
        try {
            future = __adaptee.addPaymentInstrument(
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.payment.spec.model.PaymentInstrument>() {
            @Override
            public void invoke(com.junbo.payment.spec.model.PaymentInstrument result) {
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
    
    @javax.ws.rs.DELETE
    @javax.ws.rs.Path("/payment-instruments/{paymentInstrumentId}")
    public void deletePaymentInstrument(
    @javax.ws.rs.PathParam("paymentInstrumentId") java.lang.String externalToken,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.deletePaymentInstrument(
                externalToken
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<javax.ws.rs.core.Response>() {
            @Override
            public void invoke(javax.ws.rs.core.Response result) {
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
    @javax.ws.rs.Path("/payment/{paymentInstrumentId}/authorization")
    public void postAuthorization(
    @javax.ws.rs.PathParam("paymentInstrumentId") java.lang.String piToken,
    
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.postAuthorization(
                piToken,
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
    @javax.ws.rs.Path("/payment/{paymentId}/capture")
    public void postCapture(
    @javax.ws.rs.PathParam("paymentId") java.lang.String transactionToken,
    
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.postCapture(
                transactionToken,
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
    @javax.ws.rs.Path("/payment/{paymentInstrumentId}/charge")
    public void postCharge(
    @javax.ws.rs.PathParam("paymentInstrumentId") java.lang.String piToken,
    
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.postCharge(
                piToken,
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
    @javax.ws.rs.Path("/payment/{paymentId}/reverse")
    public void reverse(
    @javax.ws.rs.PathParam("paymentId") java.lang.String transactionToken,
    
    com.junbo.payment.spec.model.PaymentTransaction request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.reverse(
                transactionToken,
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
    @javax.ws.rs.Path("/payment/{paymentId}")
    public void getById(
    @javax.ws.rs.PathParam("paymentId") java.lang.String transactionToken,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentTransaction> future;
    
        try {
            future = __adaptee.getById(
                transactionToken
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
    @javax.ws.rs.Path("/payment/search")
    public void getByOrderId(
    @javax.ws.rs.QueryParam("orderId") java.lang.String orderId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.payment.spec.model.PaymentTransaction>> future;
    
        try {
            future = __adaptee.getByOrderId(
                orderId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.payment.spec.model.PaymentTransaction>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.payment.spec.model.PaymentTransaction> result) {
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
