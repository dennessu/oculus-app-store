// CHECKSTYLE:OFF
package com.junbo.payment.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/users/{userId}/payment-instruments")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class PaymentInstrumentResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultPaymentInstrumentResource")
    private com.junbo.payment.spec.resource.PaymentInstrumentResource __adaptee;

    public com.junbo.payment.spec.resource.PaymentInstrumentResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.payment.spec.resource.PaymentInstrumentResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void postPaymentInstrument(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    com.junbo.payment.spec.model.PaymentInstrument request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentInstrument> future;
    
        try {
            future = __adaptee.postPaymentInstrument(
                userId,
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
    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{paymentInstrumentId}")
    public void getById(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("paymentInstrumentId") com.junbo.common.id.PaymentInstrumentId paymentInstrumentId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentInstrument> future;
    
        try {
            future = __adaptee.getById(
                userId,
                paymentInstrumentId
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
    @javax.ws.rs.Path("/{paymentInstrumentId}")
    public void delete(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("paymentInstrumentId") com.junbo.common.id.PaymentInstrumentId paymentInstrumentId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.delete(
                userId,
                paymentInstrumentId
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
    
    @javax.ws.rs.PUT
    @javax.ws.rs.Path("/{paymentInstrumentId}")
    public void update(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("paymentInstrumentId") com.junbo.common.id.PaymentInstrumentId paymentInstrumentId,
    
    com.junbo.payment.spec.model.PaymentInstrument request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentInstrument> future;
    
        try {
            future = __adaptee.update(
                userId,
                paymentInstrumentId,
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
    
    @javax.ws.rs.GET
    public void searchPaymentInstrument(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.BeanParam com.junbo.payment.spec.model.PaymentInstrumentSearchParam searchParam,
    
    @javax.ws.rs.BeanParam com.junbo.payment.spec.model.PageMetaData pageMetadata,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.payment.spec.model.PaymentInstrument>> future;
    
        try {
            future = __adaptee.searchPaymentInstrument(
                userId,
                searchParam,
                pageMetadata
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.payment.spec.model.PaymentInstrument>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.payment.spec.model.PaymentInstrument> result) {
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
