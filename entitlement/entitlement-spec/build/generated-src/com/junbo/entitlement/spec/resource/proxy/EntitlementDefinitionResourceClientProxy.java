
// CHECKSTYLE:OFF

package com.junbo.entitlement.spec.resource.proxy;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import com.junbo.langur.core.promise.Promise;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.base.Function;

import com.junbo.langur.core.client.ClientResponseException;
import com.junbo.langur.core.client.MessageTranscoder;
import com.junbo.langur.core.client.TypeReference;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

@org.springframework.stereotype.Component
public class EntitlementDefinitionResourceClientProxy implements com.junbo.entitlement.spec.resource.EntitlementDefinitionResource {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    public EntitlementDefinitionResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, String target) {
        this(client, transcoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public EntitlementDefinitionResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, String target, MultivaluedMap<String, Object> headers) {
        assert client != null : "client is null";
        assert transcoder != null : "transcoder is null";
        assert target != null : "target is null";
        assert headers != null : "headers is null";

        __client = client;
        __transcoder = transcoder;
        __target = target;
        __headers = headers;
    }

    
    public Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> getEntitlementDefinition(java.lang.Long entitlementDefinitionId) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlementDefinitions/{entitlementDefinitionId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
    
        
        __uriBuilder.resolveTemplate("entitlementDefinitionId", entitlementDefinitionId.toString());
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.EntitlementDefinition>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.EntitlementDefinition>decode(new TypeReference<com.junbo.entitlement.spec.model.EntitlementDefinition>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    throw new ClientResponseException(response);
                }
            }
        });
    }
    
    
    public Promise<com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition>> getEntitlementDefinitionDefinitions(java.lang.Long developerId, java.lang.String type, java.lang.String group, java.lang.String tag, com.junbo.entitlement.spec.model.PageMetadata pageMetadata) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlementDefinitions");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
    
        
        __requestBuilder.addQueryParameter("developerId", developerId.toString());
            
        __requestBuilder.addQueryParameter("type", type.toString());
            
        __requestBuilder.addQueryParameter("group", group.toString());
            
        __requestBuilder.addQueryParameter("tag", tag.toString());
            
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition>>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition>>decode(new TypeReference<com.junbo.entitlement.spec.model.ResultList<com.junbo.entitlement.spec.model.EntitlementDefinition>>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    throw new ClientResponseException(response);
                }
            }
        });
    }
    
    
    public Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> postEntitlementDefinition(com.junbo.entitlement.spec.model.EntitlementDefinition entitlementDefinition) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlementDefinitions");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __requestBuilder.setBody(__transcoder.encode(entitlementDefinition));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.EntitlementDefinition>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.EntitlementDefinition>decode(new TypeReference<com.junbo.entitlement.spec.model.EntitlementDefinition>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    throw new ClientResponseException(response);
                }
            }
        });
    }
    
    
    public Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> updateEntitlementDefinition(java.lang.Long entitlementDefinitionId, com.junbo.entitlement.spec.model.EntitlementDefinition entitlementDefinition) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlementDefinitions/{entitlementDefinitionId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePut("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("entitlementDefinitionId", entitlementDefinitionId.toString());
            
        __requestBuilder.setBody(__transcoder.encode(entitlementDefinition));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.EntitlementDefinition>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.EntitlementDefinition> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.EntitlementDefinition>decode(new TypeReference<com.junbo.entitlement.spec.model.EntitlementDefinition>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    throw new ClientResponseException(response);
                }
            }
        });
    }
    
    
    public Promise<javax.ws.rs.core.Response> deleteEntitlementDefinition(java.lang.Long entitlementDefinitionId) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlementDefinitions/{entitlementDefinitionId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareDelete("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
    
        
        __uriBuilder.resolveTemplate("entitlementDefinitionId", entitlementDefinitionId.toString());
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<javax.ws.rs.core.Response>>() {
            @Override
            public Promise<javax.ws.rs.core.Response> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<javax.ws.rs.core.Response>decode(new TypeReference<javax.ws.rs.core.Response>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    throw new ClientResponseException(response);
                }
            }
        });
    }
    
}
