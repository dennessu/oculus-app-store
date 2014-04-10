// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/end-session")
public class EndSessionEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultEndSessionEndpoint")
    private com.junbo.oauth.spec.endpoint.EndSessionEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.EndSessionEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.EndSessionEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void endSession(
    @javax.ws.rs.core.Context javax.ws.rs.core.UriInfo uriInfo,
    
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders httpHeaders,
    
    @javax.ws.rs.core.Context javax.ws.rs.container.ContainerRequestContext request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.endSession(
                uriInfo,
                httpHeaders,
                request
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
