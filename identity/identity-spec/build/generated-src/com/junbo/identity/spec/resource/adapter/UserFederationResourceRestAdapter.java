// CHECKSTYLE:OFF
package com.junbo.identity.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/users/{key1}/federations")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class UserFederationResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultUserFederationResource")
    private com.junbo.identity.spec.resource.UserFederationResource __adaptee;

    public com.junbo.identity.spec.resource.UserFederationResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.identity.spec.resource.UserFederationResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void postUserFederation(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    com.junbo.identity.spec.model.user.UserFederation userFederation,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserFederation> future;
    
        try {
            future = __adaptee.postUserFederation(
                userId,
                userFederation
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserFederation>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserFederation result) {
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
    public void getUserFederations(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.QueryParam("type") java.lang.String type,
    
    @javax.ws.rs.QueryParam("cursor") java.lang.Integer cursor,
    
    @javax.ws.rs.QueryParam("count") java.lang.Integer count,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserFederation>> future;
    
        try {
            future = __adaptee.getUserFederations(
                userId,
                type,
                cursor,
                count
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserFederation>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserFederation> result) {
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
    @javax.ws.rs.Path("/{key2}")
    public void getUserFederation(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserFederationId federationId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserFederation> future;
    
        try {
            future = __adaptee.getUserFederation(
                userId,
                federationId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserFederation>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserFederation result) {
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
    @javax.ws.rs.Path("/{key2}")
    public void updateUserFederation(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserFederationId federationId,
    
    com.junbo.identity.spec.model.user.UserFederation userFederation,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserFederation> future;
    
        try {
            future = __adaptee.updateUserFederation(
                userId,
                federationId,
                userFederation
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserFederation>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserFederation result) {
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
    @javax.ws.rs.Path("/{key2}")
    public void deleteUserFederation(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserFederationId federationId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<java.lang.Void> future;
    
        try {
            future = __adaptee.deleteUserFederation(
                userId,
                federationId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<java.lang.Void>() {
            @Override
            public void invoke(java.lang.Void result) {
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
