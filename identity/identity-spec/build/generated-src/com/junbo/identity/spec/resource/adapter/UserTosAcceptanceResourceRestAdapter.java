// CHECKSTYLE:OFF
package com.junbo.identity.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/users/{key1}/tos-acceptances")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class UserTosAcceptanceResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultUserTosAcceptanceResource")
    private com.junbo.identity.spec.resource.UserTosAcceptanceResource __adaptee;

    public com.junbo.identity.spec.resource.UserTosAcceptanceResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.identity.spec.resource.UserTosAcceptanceResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void postUserTosAcceptance(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    com.junbo.identity.spec.model.user.UserTosAcceptance userTosAcceptance,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserTosAcceptance> future;
    
        try {
            future = __adaptee.postUserTosAcceptance(
                userId,
                userTosAcceptance
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserTosAcceptance>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserTosAcceptance result) {
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
    public void getUserTosAcceptances(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.QueryParam("tos") java.lang.String tos,
    
    @javax.ws.rs.QueryParam("cursor") java.lang.Integer cursor,
    
    @javax.ws.rs.QueryParam("count") java.lang.Integer count,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserTosAcceptance>> future;
    
        try {
            future = __adaptee.getUserTosAcceptances(
                userId,
                tos,
                cursor,
                count
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserTosAcceptance>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserTosAcceptance> result) {
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
    public void getUserTosAcceptance(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserTosAcceptanceId tosAcceptanceId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserTosAcceptance> future;
    
        try {
            future = __adaptee.getUserTosAcceptance(
                userId,
                tosAcceptanceId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserTosAcceptance>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserTosAcceptance result) {
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
    public void updateUserTosAcceptance(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserTosAcceptanceId tosAcceptanceId,
    
    com.junbo.identity.spec.model.user.UserTosAcceptance userTosAcceptance,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserTosAcceptance> future;
    
        try {
            future = __adaptee.updateUserTosAcceptance(
                userId,
                tosAcceptanceId,
                userTosAcceptance
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserTosAcceptance>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserTosAcceptance result) {
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
    public void deleteUserTosAcceptance(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserTosAcceptanceId tosAcceptanceId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<java.lang.Void> future;
    
        try {
            future = __adaptee.deleteUserTosAcceptance(
                userId,
                tosAcceptanceId
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
