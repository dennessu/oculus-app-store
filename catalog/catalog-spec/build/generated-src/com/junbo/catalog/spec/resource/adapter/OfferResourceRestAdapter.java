// CHECKSTYLE:OFF
package com.junbo.catalog.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("offers")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class OfferResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultOfferResource")
    private com.junbo.catalog.spec.resource.OfferResource __adaptee;

    public com.junbo.catalog.spec.resource.OfferResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.catalog.spec.resource.OfferResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    public void getOffers(
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.offer.OffersGetOptions options,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.catalog.spec.model.offer.Offer>> future;
    
        try {
            future = __adaptee.getOffers(
                options
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.catalog.spec.model.offer.Offer>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.catalog.spec.model.offer.Offer> result) {
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
    @javax.ws.rs.Path("/{offerId}")
    public void getOffer(
    @javax.ws.rs.PathParam("offerId") com.junbo.common.id.OfferId offerId,
    
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.common.EntityGetOptions options,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.offer.Offer> future;
    
        try {
            future = __adaptee.getOffer(
                offerId,
                options
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.offer.Offer>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.offer.Offer result) {
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
    @javax.ws.rs.Path("/")
    public void create(
    com.junbo.catalog.spec.model.offer.Offer offer,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.offer.Offer> future;
    
        try {
            future = __adaptee.create(
                offer
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.offer.Offer>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.offer.Offer result) {
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
    @javax.ws.rs.Path("/{offerId}")
    public void update(
    @javax.ws.rs.PathParam("offerId") com.junbo.common.id.OfferId offerId,
    
    com.junbo.catalog.spec.model.offer.Offer offer,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.offer.Offer> future;
    
        try {
            future = __adaptee.update(
                offerId,
                offer
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.offer.Offer>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.offer.Offer result) {
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
    @javax.ws.rs.Path("/{offerId}")
    public void delete(
    @javax.ws.rs.PathParam("offerId") com.junbo.common.id.OfferId offerId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.delete(
                offerId
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
}
