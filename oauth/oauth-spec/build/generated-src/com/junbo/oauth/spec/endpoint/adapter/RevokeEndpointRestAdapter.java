// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/revoke")
@javax.ws.rs.Consumes({"application/x-www-form-urlencoded"})
public class RevokeEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultRevokeEndpoint")
    private com.junbo.oauth.spec.endpoint.RevokeEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.RevokeEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.RevokeEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void revoke(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.FormParam("token") java.lang.String token,
    
    @javax.ws.rs.FormParam("token_type_hint") java.lang.String tokenTypeHint,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.revoke(
                authorization,
                token,
                tokenTypeHint
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
    @javax.ws.rs.Path("/consent")
    public void revokeConsent(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    
    @javax.ws.rs.FormParam("client_id") java.lang.String clientId,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.revokeConsent(
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
}
