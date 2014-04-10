// CHECKSTYLE:OFF
package com.junbo.rating.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/rating")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class RatingResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultRatingResource")
    private com.junbo.rating.spec.resource.RatingResource __adaptee;

    public com.junbo.rating.spec.resource.RatingResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.rating.spec.resource.RatingResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Path("/offers")
    public void offerRating(
    @javax.validation.Valid com.junbo.rating.spec.model.request.OfferRatingRequest request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.rating.spec.model.request.OfferRatingRequest> future;
    
        try {
            future = __adaptee.offerRating(
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.rating.spec.model.request.OfferRatingRequest>() {
            @Override
            public void invoke(com.junbo.rating.spec.model.request.OfferRatingRequest result) {
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
    @javax.ws.rs.Path("/order")
    public void orderRating(
    @javax.validation.Valid com.junbo.rating.spec.model.request.OrderRatingRequest request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.rating.spec.model.request.OrderRatingRequest> future;
    
        try {
            future = __adaptee.orderRating(
                request
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.rating.spec.model.request.OrderRatingRequest>() {
            @Override
            public void invoke(com.junbo.rating.spec.model.request.OrderRatingRequest result) {
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
