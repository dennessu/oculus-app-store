// CHECKSTYLE:OFF
package com.junbo.identity.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/users/{key1}/device-profiles")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class UserDeviceProfileResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultUserDeviceProfileResource")
    private com.junbo.identity.spec.resource.UserDeviceProfileResource __adaptee;

    public com.junbo.identity.spec.resource.UserDeviceProfileResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.identity.spec.resource.UserDeviceProfileResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void postUserDeviceProfile(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    com.junbo.identity.spec.model.user.UserDeviceProfile userProfile,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserDeviceProfile> future;
    
        try {
            future = __adaptee.postUserDeviceProfile(
                userId,
                userProfile
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserDeviceProfile>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserDeviceProfile result) {
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
    public void getUserDeviceProfiles(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.QueryParam("type") java.lang.String type,
    
    @javax.ws.rs.QueryParam("cursor") java.lang.Integer cursor,
    
    @javax.ws.rs.QueryParam("count") java.lang.Integer count,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserDeviceProfile>> future;
    
        try {
            future = __adaptee.getUserDeviceProfiles(
                userId,
                type,
                cursor,
                count
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserDeviceProfile>>() {
            @Override
            public void invoke(com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserDeviceProfile> result) {
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
    public void getUserDeviceProfile(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserDeviceProfileId deviceProfileId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserDeviceProfile> future;
    
        try {
            future = __adaptee.getUserDeviceProfile(
                userId,
                deviceProfileId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserDeviceProfile>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserDeviceProfile result) {
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
    public void updateUserDeviceProfile(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserDeviceProfileId deviceProfileId,
    
    com.junbo.identity.spec.model.user.UserDeviceProfile userDeviceProfile,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.user.UserDeviceProfile> future;
    
        try {
            future = __adaptee.updateUserDeviceProfile(
                userId,
                deviceProfileId,
                userDeviceProfile
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.user.UserDeviceProfile>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.user.UserDeviceProfile result) {
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
    public void deleteUserDeviceProfile(
    @javax.ws.rs.PathParam("key1") com.junbo.common.id.UserId userId,
    
    @javax.ws.rs.PathParam("key2") com.junbo.common.id.UserDeviceProfileId deviceProfileId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<java.lang.Void> future;
    
        try {
            future = __adaptee.deleteUserDeviceProfile(
                userId,
                deviceProfileId
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
