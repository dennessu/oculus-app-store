
// CHECKSTYLE:OFF

package com.junbo.identity.spec.resource.proxy;

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
public class UserOptInResourceClientProxy implements com.junbo.identity.spec.resource.UserOptInResource {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    private final PathParamTranscoder __pathParamTranscoder;

    private final QueryParamTranscoder __queryParamTranscoder;

    public UserOptInResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
                                QueryParamTranscoder queryParamTranscoder, String target) {
        this(client, transcoder, pathParamTranscoder, queryParamTranscoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public UserOptInResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
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

    
    public Promise<com.junbo.identity.spec.model.user.UserOptIn> postUserOptIn(com.junbo.common.id.UserId userId, com.junbo.identity.spec.model.user.UserOptIn userOptIn) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/users/{key1}/opt-ins");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("key1", __pathParamTranscoder.encode(userId));
            
        __requestBuilder.setBody(__transcoder.encode(userOptIn));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.identity.spec.model.user.UserOptIn>>() {
            @Override
            public Promise<com.junbo.identity.spec.model.user.UserOptIn> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.identity.spec.model.user.UserOptIn>decode(new TypeReference<com.junbo.identity.spec.model.user.UserOptIn>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserOptIn>> getUserOptIns(com.junbo.common.id.UserId userId, java.lang.String type, java.lang.Integer cursor, java.lang.Integer count) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/users/{key1}/opt-ins");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("key1", __pathParamTranscoder.encode(userId));
            
        if (type != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(type));
        }
            
        if (cursor != null) {
            __requestBuilder.addQueryParameter("cursor", __queryParamTranscoder.encode(cursor));
        }
            
        if (count != null) {
            __requestBuilder.addQueryParameter("count", __queryParamTranscoder.encode(count));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserOptIn>>>() {
            @Override
            public Promise<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserOptIn>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserOptIn>>decode(new TypeReference<com.junbo.common.model.Results<com.junbo.identity.spec.model.user.UserOptIn>>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.identity.spec.model.user.UserOptIn> getUserOptIn(com.junbo.common.id.UserId userId, com.junbo.common.id.UserOptInId optInId) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/users/{key1}/opt-ins/{key2}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("key1", __pathParamTranscoder.encode(userId));
            
        __uriBuilder.resolveTemplate("key2", __pathParamTranscoder.encode(optInId));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.identity.spec.model.user.UserOptIn>>() {
            @Override
            public Promise<com.junbo.identity.spec.model.user.UserOptIn> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.identity.spec.model.user.UserOptIn>decode(new TypeReference<com.junbo.identity.spec.model.user.UserOptIn>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.identity.spec.model.user.UserOptIn> updateUserOptIn(com.junbo.common.id.UserId userId, com.junbo.common.id.UserOptInId optInId, com.junbo.identity.spec.model.user.UserOptIn userOptIn) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/users/{key1}/opt-ins/{key2}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePut("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("key1", __pathParamTranscoder.encode(userId));
            
        __uriBuilder.resolveTemplate("key2", __pathParamTranscoder.encode(optInId));
            
        __requestBuilder.setBody(__transcoder.encode(userOptIn));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.identity.spec.model.user.UserOptIn>>() {
            @Override
            public Promise<com.junbo.identity.spec.model.user.UserOptIn> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.identity.spec.model.user.UserOptIn>decode(new TypeReference<com.junbo.identity.spec.model.user.UserOptIn>() {}, response.getResponseBody()));
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
    
    
    public Promise<java.lang.Void> deleteUserOptIn(com.junbo.common.id.UserId userId, com.junbo.common.id.UserOptInId optInId) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("/users/{key1}/opt-ins/{key2}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareDelete("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("key1", __pathParamTranscoder.encode(userId));
            
        __uriBuilder.resolveTemplate("key2", __pathParamTranscoder.encode(optInId));
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<java.lang.Void>>() {
            @Override
            public Promise<java.lang.Void> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<java.lang.Void>decode(new TypeReference<java.lang.Void>() {}, response.getResponseBody()));
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
