// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/userinfo")
@javax.ws.rs.Produces({"application/json"})
public class UserInfoEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultUserInfoEndpoint")
    private com.junbo.oauth.spec.endpoint.UserInfoEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.UserInfoEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.UserInfoEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void getUserInfo(
    @javax.ws.rs.HeaderParam("Authorization") java.lang.String authorization,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<com.junbo.oauth.spec.model.UserInfo> future;
    
        try {
            future = __adaptee.getUserInfo(
                authorization
            );
        } catch (Throwable ex) {
            __asyncResponse.resume(ex);
            return;
        }
    
        future.onSuccess(new com.junbo.langur.core.promise.Promise.Callback<com.junbo.oauth.spec.model.UserInfo>() {
            @Override
            public void invoke(com.junbo.oauth.spec.model.UserInfo result) {
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
