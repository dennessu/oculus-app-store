
// CHECKSTYLE:OFF

package com.junbo.entitlement.spec.resource.proxy;

import com.junbo.common.error.AppError;
import com.junbo.common.error.AppErrorException;
import com.junbo.common.error.Error;
import com.junbo.langur.core.client.*;
import com.junbo.langur.core.promise.Promise;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

@org.springframework.stereotype.Component
public class EntitlementResourceClientProxy implements com.junbo.entitlement.spec.resource.EntitlementResource {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    private final PathParamTranscoder __pathParamTranscoder;

    private final QueryParamTranscoder __queryParamTranscoder;

    public EntitlementResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
                                QueryParamTranscoder queryParamTranscoder, String target) {
        this(client, transcoder, pathParamTranscoder, queryParamTranscoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public EntitlementResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
                            QueryParamTranscoder queryParamTranscoder, String target, MultivaluedMap<String, Object> headers) {
        assert client != null : "client is null";
        assert transcoder != null : "transcoder is null";
        assert pathParamTranscoder != null : "pathParamTranscoder is null";
        assert queryParamTranscoder != null : "queryParamTranscoder is null";
        assert target != null : "target is null";
        assert headers != null : "headers is null";

        __client = client;
        __transcoder = transcoder;
        __pathParamTranscoder = pathParamTranscoder;
        __queryParamTranscoder = queryParamTranscoder;
        __target = target;
        __headers = headers;
    }

    
    public Promise<com.junbo.entitlement.spec.model.Entitlement> getEntitlement(com.junbo.common.id.EntitlementId entitlementId) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlements/{entitlementId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
    
        
        __uriBuilder.resolveTemplate("entitlementId", __pathParamTranscoder.encode(entitlementId));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.Entitlement>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.Entitlement> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.Entitlement>decode(new TypeReference<com.junbo.entitlement.spec.model.Entitlement>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    try {
                        Error error = __transcoder.<Error>decode(new TypeReference<Error>() {}, response.getResponseBody());
                        throw getAppError(response.getStatusCode(), error).exception();
                    } catch (java.io.IOException e) {
                        throw new ClientResponseException("Unable to unmarshall the error response", response, e);
                    }
                }
            }
        });
    }
    
    
    public Promise<com.junbo.entitlement.spec.model.Entitlement> postEntitlement(com.junbo.entitlement.spec.model.Entitlement entitlement) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlements");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __requestBuilder.setBody(__transcoder.encode(entitlement));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.Entitlement>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.Entitlement> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.Entitlement>decode(new TypeReference<com.junbo.entitlement.spec.model.Entitlement>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    try {
                        Error error = __transcoder.<Error>decode(new TypeReference<Error>() {}, response.getResponseBody());
                        throw getAppError(response.getStatusCode(), error).exception();
                    } catch (java.io.IOException e) {
                        throw new ClientResponseException("Unable to unmarshall the error response", response, e);
                    }
                }
            }
        });
    }
    
    
    public Promise<com.junbo.entitlement.spec.model.Entitlement> updateEntitlement(com.junbo.common.id.EntitlementId entitlementId, com.junbo.entitlement.spec.model.Entitlement entitlement) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlements/{entitlementId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePut("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("entitlementId", __pathParamTranscoder.encode(entitlementId));
            
        __requestBuilder.setBody(__transcoder.encode(entitlement));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.Entitlement>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.Entitlement> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.Entitlement>decode(new TypeReference<com.junbo.entitlement.spec.model.Entitlement>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    try {
                        Error error = __transcoder.<Error>decode(new TypeReference<Error>() {}, response.getResponseBody());
                        throw getAppError(response.getStatusCode(), error).exception();
                    } catch (java.io.IOException e) {
                        throw new ClientResponseException("Unable to unmarshall the error response", response, e);
                    }
                }
            }
        });
    }
    
    
    public Promise<javax.ws.rs.core.Response> deleteEntitlement(com.junbo.common.id.EntitlementId entitlementId) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlements/{entitlementId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareDelete("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
    
        
        __uriBuilder.resolveTemplate("entitlementId", __pathParamTranscoder.encode(entitlementId));
        
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
                    try {
                        Error error = __transcoder.<Error>decode(new TypeReference<Error>() {}, response.getResponseBody());
                        throw getAppError(response.getStatusCode(), error).exception();
                    } catch (java.io.IOException e) {
                        throw new ClientResponseException("Unable to unmarshall the error response", response, e);
                    }
                }
            }
        });
    }
    
    
    public Promise<com.junbo.entitlement.spec.model.Entitlement> transferEntitlement(com.junbo.entitlement.spec.model.EntitlementTransfer entitlementTransfer) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/entitlements/transfer");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __requestBuilder.setBody(__transcoder.encode(entitlementTransfer));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.entitlement.spec.model.Entitlement>>() {
            @Override
            public Promise<com.junbo.entitlement.spec.model.Entitlement> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.entitlement.spec.model.Entitlement>decode(new TypeReference<com.junbo.entitlement.spec.model.Entitlement>() {}, response.getResponseBody()));
                    } catch (java.io.IOException ex) {
                         throw new RuntimeException(ex);
                    }
                } else {
                    try {
                        Error error = __transcoder.<Error>decode(new TypeReference<Error>() {}, response.getResponseBody());
                        throw getAppError(response.getStatusCode(), error).exception();
                    } catch (java.io.IOException e) {
                        throw new ClientResponseException("Unable to unmarshall the error response", response, e);
                    }
                }
            }
        });
    }
    

    private AppError getAppError(final int statusCode, final Error error) {
        return new AppError() {
            @Override
            public int getHttpStatusCode() {
                return statusCode;
            }

            @Override
            public String getCode() {
                return error.getCode();
            }

            @Override
            public String getDescription() {
                return error.getDescription();
            }

            @Override
            public String getField() {
                return error.getField();
            }

            @Override
            public List<AppError> getCauses() {
                List<AppError> causes = new ArrayList<>();
                if (error.getCauses() != null) {
                    for (Error innerError : error.getCauses()) {
                        causes.add(getAppError(statusCode, innerError));
                    }
                }
                return causes;
            }

            @Override
            public AppErrorException exception() {
                return new AppErrorException(this);
            }
        };
    }
}
