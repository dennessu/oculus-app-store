// CHECKSTYLE:OFF
package com.junbo.entitlement.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/entitlements")
@javax.ws.rs.Produces({"application/json"})
public class EntitlementResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultEntitlementResource")
    private com.junbo.entitlement.spec.resource.EntitlementResource __adaptee;

    public com.junbo.entitlement.spec.resource.EntitlementResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.entitlement.spec.resource.EntitlementResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{entitlementId}")
    public void getEntitlement(
    @javax.ws.rs.PathParam("entitlementId") com.junbo.common.id.EntitlementId entitlementId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.Entitlement> future;
    
        try {
            future = __adaptee.getEntitlement(
                entitlementId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.Entitlement>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.Entitlement result) {
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
    @javax.ws.rs.Consumes({"application/json"})
    public void postEntitlement(
    @javax.validation.Valid com.junbo.entitlement.spec.model.Entitlement entitlement,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.Entitlement> future;
    
        try {
            future = __adaptee.postEntitlement(
                entitlement
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.Entitlement>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.Entitlement result) {
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
    @javax.ws.rs.Path("/{entitlementId}")
    @javax.ws.rs.Consumes({"application/json"})
    public void updateEntitlement(
    @javax.ws.rs.PathParam("entitlementId") com.junbo.common.id.EntitlementId entitlementId,
    
    @javax.validation.Valid com.junbo.entitlement.spec.model.Entitlement entitlement,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.Entitlement> future;
    
        try {
            future = __adaptee.updateEntitlement(
                entitlementId,
                entitlement
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.Entitlement>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.Entitlement result) {
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
    @javax.ws.rs.Path("/{entitlementId}")
    public void deleteEntitlement(
    @javax.ws.rs.PathParam("entitlementId") com.junbo.common.id.EntitlementId entitlementId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.deleteEntitlement(
                entitlementId
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
    @javax.ws.rs.Path("/transfer")
    @javax.ws.rs.Consumes({"application/json"})
    public void transferEntitlement(
    @javax.validation.Valid com.junbo.entitlement.spec.model.EntitlementTransfer entitlementTransfer,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.Entitlement> future;
    
        try {
            future = __adaptee.transferEntitlement(
                entitlementTransfer
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.Entitlement>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.Entitlement result) {
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
