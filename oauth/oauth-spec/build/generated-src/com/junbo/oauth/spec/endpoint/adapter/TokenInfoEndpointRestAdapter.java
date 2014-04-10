// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/tokeninfo")
@javax.ws.rs.Produces({"application/json"})
@javax.ws.rs.Consumes({"application/json"})
public class TokenInfoEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultTokenInfoEndpoint")
    private com.junbo.oauth.spec.endpoint.TokenInfoEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.TokenInfoEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.TokenInfoEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getTokenInfo(
    @javax.ws.rs.QueryParam("access_token") java.lang.String tokenValue,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.TokenInfo> future;
    
        try {
            future = __adaptee.getTokenInfo(
                tokenValue
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.TokenInfo>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.TokenInfo result) {
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
