// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/clients")
@javax.ws.rs.Produces({"application/json"})
public class ClientEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultClientEndpoint")
    private com.junbo.oauth.spec.endpoint.ClientEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.ClientEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.ClientEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    @javax.ws.rs.Consumes({"application/json"})
    public void postClient(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    com.junbo.oauth.spec.model.Client client,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Client> future;
    
        try {
            future = __adaptee.postClient(
                authorization,
                client
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Client>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Client result) {
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
    @javax.ws.rs.Path("/{clientId}")
    public void getByClientId(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("clientId") java.lang.String clientId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Client> future;
    
        try {
            future = __adaptee.getByClientId(
                authorization,
                clientId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Client>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Client result) {
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
    @javax.ws.rs.Path("/{clientId}/client-info")
    public void getInfoByClientId(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("clientId") java.lang.String clientId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Client> future;
    
        try {
            future = __adaptee.getInfoByClientId(
                authorization,
                clientId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Client>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Client result) {
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
    @javax.ws.rs.Path("/{clientId}")
    @javax.ws.rs.Consumes({"application/json"})
    public void putClient(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("clientId") java.lang.String clientId,
    
    com.junbo.oauth.spec.model.Client client,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Client> future;
    
        try {
            future = __adaptee.putClient(
                authorization,
                clientId,
                client
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Client>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Client result) {
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
    @javax.ws.rs.Path("/{clientId}")
    public void deleteClient(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("clientId") java.lang.String clientId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.deleteClient(
                authorization,
                clientId
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
    @javax.ws.rs.Path("/{clientId}/reset-secret")
    public void resetSecret(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.PathParam("clientId") java.lang.String clientId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.Client> future;
    
        try {
            future = __adaptee.resetSecret(
                authorization,
                clientId
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.Client>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.Client result) {
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
