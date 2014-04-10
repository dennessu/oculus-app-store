// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/token")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/x-www-form-urlencoded"})
public class TokenEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultTokenEndpoint")
    private com.junbo.oauth.spec.endpoint.TokenEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.TokenEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.TokenEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.POST
    public void postToken(
    javax.ws.rs.core.MultivaluedMap<java.lang.String,java.lang.String> formParams,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.AccessTokenResponse> future;
    
        try {
            future = __adaptee.postToken(
                formParams
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.AccessTokenResponse>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.AccessTokenResponse result) {
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
    @javax.ws.rs.Path("/explicit")
    public void postToken(
    @javax.ws.rs.FormParam("client_id") java.lang.String clientId,
    
    @javax.ws.rs.FormParam("client_secret") java.lang.String clientSecret,
    
    @javax.ws.rs.FormParam("grant_type") java.lang.String grantType,
    
    @javax.ws.rs.FormParam("code") java.lang.String code,
    
    @javax.ws.rs.FormParam("scope") java.lang.String scope,
    
    @javax.ws.rs.FormParam("redirect_uri") java.lang.String redirectUri,
    
    @javax.ws.rs.FormParam("username") java.lang.String username,
    
    @javax.ws.rs.FormParam("password") java.lang.String password,
    
    @javax.ws.rs.FormParam("refresh_token") java.lang.String refreshToken,
    
    @javax.ws.rs.FormParam("nonce") java.lang.String nonce,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.AccessTokenResponse> future;
    
        try {
            future = __adaptee.postToken(
                clientId,
                clientSecret,
                grantType,
                code,
                scope,
                redirectUri,
                username,
                password,
                refreshToken,
                nonce
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.AccessTokenResponse>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.AccessTokenResponse result) {
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
