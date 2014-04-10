// CHECKSTYLE:OFF
package com.junbo.oauth.spec.endpoint.adapter;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
@javax.ws.rs.Path("/oauth2/authorize")
@javax.ws.rs.Produces({"application/json"})
public class AuthorizeEndpointRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("defaultAuthorizeEndpoint")
    private com.junbo.oauth.spec.endpoint.AuthorizeEndpoint __adaptee;

    public com.junbo.oauth.spec.endpoint.AuthorizeEndpoint getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(com.junbo.oauth.spec.endpoint.AuthorizeEndpoint adaptee) {
        __adaptee = adaptee;
    }

    
    @javax.ws.rs.GET
    public void authorize(
    @javax.ws.rs.core.Context javax.ws.rs.core.UriInfo uriInfo,
    
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders httpHeaders,
    
    @javax.ws.rs.core.Context javax.ws.rs.container.ContainerRequestContext request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.authorize(
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
    
    @javax.ws.rs.POST
    @javax.ws.rs.Consumes({"application/x-www-form-urlencoded"})
    public void postAuthorize(
    @javax.ws.rs.core.Context javax.ws.rs.core.HttpHeaders httpHeaders,
    
    javax.ws.rs.core.MultivaluedMap<java.lang.String,java.lang.String> formParams,
    
    @javax.ws.rs.core.Context javax.ws.rs.container.ContainerRequestContext request,
    @javax.ws.rs.container.Suspended final javax.ws.rs.container.AsyncResponse __asyncResponse) {
    
        com.junbo.langur.core.promise.Promise<javax.ws.rs.core.Response> future;
    
        try {
            future = __adaptee.postAuthorize(
                httpHeaders,
                formParams,
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
