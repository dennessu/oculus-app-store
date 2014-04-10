// CHECKSTYLE:OFF
package com.junbo.catalog.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("promotions")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class PromotionResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultPromotionResource")
    private com.junbo.catalog.spec.resource.PromotionResource __adaptee;

    public com.junbo.catalog.spec.resource.PromotionResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.catalog.spec.resource.PromotionResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/")
    public void getPromotions(
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.promotion.PromotionsGetOptions options,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.catalog.spec.model.promotion.Promotion>> future;
    
        try {
            future = __adaptee.getPromotions(
                options
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.catalog.spec.model.promotion.Promotion>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.catalog.spec.model.promotion.Promotion> result) {
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
    @javax.ws.rs.Path("/{promotionId}")
    public void getPromotion(
    @javax.ws.rs.PathParam("promotionId") com.junbo.common.id.PromotionId promotionId,
    
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.common.EntityGetOptions options,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.promotion.Promotion> future;
    
        try {
            future = __adaptee.getPromotion(
                promotionId,
                options
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.promotion.Promotion>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.promotion.Promotion result) {
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
    com.junbo.catalog.spec.model.promotion.Promotion promotion,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.promotion.Promotion> future;
    
        try {
            future = __adaptee.create(
                promotion
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.promotion.Promotion>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.promotion.Promotion result) {
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
    @javax.ws.rs.Path("/{promotionId}")
    public void update(
    @javax.ws.rs.PathParam("promotionId") com.junbo.common.id.PromotionId promotionId,
    
    com.junbo.catalog.spec.model.promotion.Promotion promotion,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.promotion.Promotion> future;
    
        try {
            future = __adaptee.update(
                promotionId,
                promotion
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.promotion.Promotion>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.promotion.Promotion result) {
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
    @javax.ws.rs.Path("/{promotionId}")
    public void delete(
    @javax.ws.rs.PathParam("promotionId") com.junbo.common.id.PromotionId promotionId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.delete(
                promotionId
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
