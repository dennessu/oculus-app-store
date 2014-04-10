// CHECKSTYLE:OFF
package com.junbo.identity.spec.resource.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("identity/apps")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class AppResourceRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultAppResource")
    private com.junbo.identity.spec.resource.AppResource __adaptee;

    public com.junbo.identity.spec.resource.AppResource getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.identity.spec.resource.AppResource adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    @javax.ws.rs.Path("/{appId}")
    public void getApp(
    @javax.ws.rs.PathParam("appId") com.junbo.common.id.AppId appId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.app.App> future;
    
        try {
            future = __adaptee.getApp(
                appId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.app.App>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.app.App result) {
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
    public void postApp(
    com.junbo.identity.spec.model.app.App app,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.app.App> future;
    
        try {
            future = __adaptee.postApp(
                app
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.app.App>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.app.App result) {
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
    @javax.ws.rs.Path("/{appId}")
    public void updateApp(
    @javax.ws.rs.PathParam("appId") com.junbo.common.id.AppId appId,
    
    com.junbo.identity.spec.model.app.App app,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.app.App> future;
    
        try {
            future = __adaptee.updateApp(
                appId,
                app
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.app.App>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.app.App result) {
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
    @javax.ws.rs.Path("/{appId}")
    public void deleteApp(
    @javax.ws.rs.PathParam("appId") com.junbo.common.id.AppId appId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.identity.spec.model.app.App> future;
    
        try {
            future = __adaptee.deleteApp(
                appId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.identity.spec.model.app.App>() {
            @Override
            public void invoke(com.junbo.identity.spec.model.app.App result) {
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
