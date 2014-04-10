// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/scopes")
@javax.ws.rs.Produces({"application/json"})
public class ScopeEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultScopeEndpoint")
    private com.junbo.oauth.spec.endpoint.ScopeEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.ScopeEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.ScopeEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Consumes({"application/json"})
    public void postScope(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    com.junbo.oauth.spec.model.Scope scope,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Scope> future;
    
        try {
            future = __adaptee.postScope(
                authorization,
                scope
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Scope>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Scope result) {
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
    @javax.ws.rs.Path("/{scopeName}")
    public void getScope(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("scopeName") java.lang.String scopeName,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Scope> future;
    
        try {
            future = __adaptee.getScope(
                authorization,
                scopeName
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Scope>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Scope result) {
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
    public void getByScopeNames(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.QueryParam("scopeNames") java.lang.String scopeNames,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<java.util.List<com.junbo.oauth.spec.model.Scope>> future;
    
        try {
            future = __adaptee.getByScopeNames(
                authorization,
                scopeNames
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<java.util.List<com.junbo.oauth.spec.model.Scope>>() {
            @Override
            public void invoke(java.util.List<com.junbo.oauth.spec.model.Scope> result) {
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
    @javax.ws.rs.Path("/{scopeName}")
    @javax.ws.rs.Consumes({"application/json"})
    public void putScope(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("scopeName") java.lang.String scopeName,
    
    com.junbo.oauth.spec.model.Scope scope,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Scope> future;
    
        try {
            future = __adaptee.putScope(
                authorization,
                scopeName,
                scope
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Scope>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Scope result) {
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
