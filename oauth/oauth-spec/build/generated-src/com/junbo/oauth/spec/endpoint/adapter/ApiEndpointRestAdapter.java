// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/apis")
@javax.ws.rs.Produces({"application/json"})
public class ApiEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultApiEndpoint")
    private com.junbo.oauth.spec.endpoint.ApiEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.ApiEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.ApiEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getAllApis(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<java.util.List<com.junbo.oauth.spec.model.ApiDefinition>> future;
    
        try {
            future = __adaptee.getAllApis(
                authorization
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<java.util.List<com.junbo.oauth.spec.model.ApiDefinition>>() {
            @Override
            public void invoke(java.util.List<com.junbo.oauth.spec.model.ApiDefinition> result) {
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
    @javax.ws.rs.Path("/{apiName}")
    public void getApi(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("apiName") java.lang.String apiName,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.ApiDefinition> future;
    
        try {
            future = __adaptee.getApi(
                authorization,
                apiName
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.ApiDefinition>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.ApiDefinition result) {
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
    public void postApi(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    com.junbo.oauth.spec.model.ApiDefinition apiDefinition,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.ApiDefinition> future;
    
        try {
            future = __adaptee.postApi(
                authorization,
                apiDefinition
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.ApiDefinition>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.ApiDefinition result) {
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
    @javax.ws.rs.Path("/{apiName}")
    public void putApi(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("apiName") java.lang.String apiName,
    
    com.junbo.oauth.spec.model.ApiDefinition apiDefinition,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.ApiDefinition> future;
    
        try {
            future = __adaptee.putApi(
                authorization,
                apiName,
                apiDefinition
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.ApiDefinition>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.ApiDefinition result) {
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
    @javax.ws.rs.Path("/{apiName}")
    public void deleteApi(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("apiName") java.lang.String apiName,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.deleteApi(
                authorization,
                apiName
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
