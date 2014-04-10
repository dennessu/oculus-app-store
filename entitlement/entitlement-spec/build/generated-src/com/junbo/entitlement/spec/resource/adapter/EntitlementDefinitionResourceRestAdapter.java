// CHECKSTYLE:OFF
package com.junbo.entitlement.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/entitlementDefinitions")
@javax.ws.rs.Produces({"application/json"})
public class EntitlementDefinitionResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultEntitlementDefinitionResource")
    private com.junbo.entitlement.spec.resource.EntitlementDefinitionResource __adaptee;

    public com.junbo.entitlement.spec.resource.EntitlementDefinitionResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.entitlement.spec.resource.EntitlementDefinitionResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{entitlementDefinitionId}")
    public void getEntitlementDefinition(
    @javax.ws.rs.PathParam("entitlementDefinitionId") java.lang.Long entitlementDefinitionId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> future;
    
        try {
            future = __adaptee.getEntitlementDefinition(
                entitlementDefinitionId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.EntitlementDefinition>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.EntitlementDefinition result) {
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
    public void getEntitlementDefinitionDefinitions(
    @javax.ws.rs.QueryParam("developerId") java.lang.Long developerId,
    
    @javax.ws.rs.QueryParam("type") java.lang.String type,
    
    @javax.ws.rs.QueryParam("group") java.lang.String group,
    
    @javax.ws.rs.QueryParam("tag") java.lang.String tag,
    
    @javax.ws.rs.BeanParam com.junbo.entitlement.spec.model.PageMetadata pageMetadata,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition>> future;
    
        try {
            future = __adaptee.getEntitlementDefinitionDefinitions(
                developerId,
                type,
                group,
                tag,
                pageMetadata
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition>>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition> result) {
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
    public void postEntitlementDefinition(
    @javax.validation.Valid com.junbo.entitlement.spec.model.EntitlementDefinition entitlementDefinition,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> future;
    
        try {
            future = __adaptee.postEntitlementDefinition(
                entitlementDefinition
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.EntitlementDefinition>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.EntitlementDefinition result) {
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
    @javax.ws.rs.Path("/{entitlementDefinitionId}")
    @javax.ws.rs.Consumes({"application/json"})
    public void updateEntitlementDefinition(
    @javax.ws.rs.PathParam("entitlementDefinitionId") java.lang.Long entitlementDefinitionId,
    
    @javax.validation.Valid com.junbo.entitlement.spec.model.EntitlementDefinition entitlementDefinition,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> future;
    
        try {
            future = __adaptee.updateEntitlementDefinition(
                entitlementDefinitionId,
                entitlementDefinition
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.entitlement.spec.model.EntitlementDefinition>() {
            @Override
            public void invoke(com.junbo.entitlement.spec.model.EntitlementDefinition result) {
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
    @javax.ws.rs.Path("/{entitlementDefinitionId}")
    public void deleteEntitlementDefinition(
    @javax.ws.rs.PathParam("entitlementDefinitionId") java.lang.Long entitlementDefinitionId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.deleteEntitlementDefinition(
                entitlementDefinitionId
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
