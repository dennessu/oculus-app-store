// CHECKSTYLE:OFF
package com.junbo.catalog.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/entitlement-definitions")
@javax.ws.rs.Produces({"application/json"})
public class EntitlementDefinitionResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultEntitlementDefinitionResource")
    private com.junbo.catalog.spec.resource.EntitlementDefinitionResource __adaptee;

    public com.junbo.catalog.spec.resource.EntitlementDefinitionResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.catalog.spec.resource.EntitlementDefinitionResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{entitlementDefinitionId}")
    public void getEntitlementDefinition(
    @javax.ws.rs.PathParam("entitlementDefinitionId") com.junbo.common.id.EntitlementDefinitionId entitlementDefinitionId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition> future;
    
        try {
            future = __adaptee.getEntitlementDefinition(
                entitlementDefinitionId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition result) {
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
    @javax.validation.constraints.NotNull @javax.ws.rs.QueryParam("developerId") com.junbo.common.id.UserId developerId,
    
    @javax.ws.rs.QueryParam("type") java.lang.String type,
    
    @javax.ws.rs.QueryParam("group") java.lang.String group,
    
    @javax.ws.rs.QueryParam("tag") java.lang.String tag,
    
    @javax.ws.rs.BeanParam com.junbo.catalog.spec.model.common.PageableGetOptions pageMetadata,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition>> future;
    
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
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition> result) {
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
    @javax.validation.Valid com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition entitlementDefinition,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition> future;
    
        try {
            future = __adaptee.postEntitlementDefinition(
                entitlementDefinition
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition>() {
            @Override
            public void invoke(com.junbo.catalog.spec.model.entitlementdef.EntitlementDefinition result) {
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
