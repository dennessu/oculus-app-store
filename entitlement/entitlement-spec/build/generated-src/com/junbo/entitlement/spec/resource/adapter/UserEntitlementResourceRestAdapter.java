// CHECKSTYLE:OFF
package com.junbo.entitlement.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Path("users/{userId}/entitlements")
public class UserEntitlementResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultUserEntitlementResource")
    private com.junbo.entitlement.spec.resource.UserEntitlementResource __adaptee;

    public com.junbo.entitlement.spec.resource.UserEntitlementResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.entitlement.spec.resource.UserEntitlementResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getEntitlements(
    @javax.ws.rs.PathParam("userId") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.BeanParam com.junbo.entitlement.spec.model.EntitlementSearchParam searchParam,
    
    @javax.ws.rs.BeanParam com.junbo.entitlement.spec.model.PageMetadata pageMetadata,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement>> future;
    
        try {
            future = __adaptee.getEntitlements(
                userId,
                searchParam,
                pageMetadata
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement> result) {
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
