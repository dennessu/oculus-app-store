// CHECKSTYLE:OFF
package com.junbo.payment.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/payment-instrument-types")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class PaymentInstrumentTypeResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultPaymentInstrumentTypeResource")
    private com.junbo.payment.spec.resource.PaymentInstrumentTypeResource __adaptee;

    public com.junbo.payment.spec.resource.PaymentInstrumentTypeResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.payment.spec.resource.PaymentInstrumentTypeResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{paymentInstrumentType}")
    public void getById(
    @javax.ws.rs.PathParam("paymentInstrumentType") java.lang.String paymentInstrumentType,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.payment.spec.model.PaymentInstrumentType> future;
    
        try {
            future = __adaptee.getById(
                paymentInstrumentType
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.payment.spec.model.PaymentInstrumentType>() {
            @Override
            public void invoke(com.junbo.payment.spec.model.PaymentInstrumentType result) {
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
