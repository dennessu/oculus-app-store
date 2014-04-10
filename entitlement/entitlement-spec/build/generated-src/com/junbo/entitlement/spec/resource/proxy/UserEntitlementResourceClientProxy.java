
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
public class UserEntitlementResourceClientProxy implements com.junbo.entitlement.spec.resource.UserEntitlementResource {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    private final PathParamTranscoder __pathParamTranscoder;

    private final QueryParamTranscoder __queryParamTranscoder;

    public UserEntitlementResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
                                QueryParamTranscoder queryParamTranscoder, String target) {
        this(client, transcoder, pathParamTranscoder, queryParamTranscoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public UserEntitlementResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
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

    
    public Promise<com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement>> getEntitlements(com.junbo.common.id.UserId userId, com.junbo.entitlement.spec.model.EntitlementSearchParam searchParam, com.junbo.entitlement.spec.model.PageMetadata pageMetadata) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("users/{userId}/entitlements");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
    
        
        __uriBuilder.resolveTemplate("userId", __pathParamTranscoder.encode(userId));
            
        
        if (searchParam.getUserId() != null) {
            __requestBuilder.addQueryParameter("userId", __queryParamTranscoder.encode(searchParam.getUserId()));
        }
        
        if (searchParam.getDeveloperId() != null) {
            __requestBuilder.addQueryParameter("developerId", __queryParamTranscoder.encode(searchParam.getDeveloperId()));
        }
        
        if (searchParam.getOfferIds() != null) {
        for (com.junbo.common.id.OfferId __item : searchParam.getOfferIds()) {
        __requestBuilder.addQueryParameter("offerIds", __queryParamTranscoder.encode(__item));
        }
        }
        
        if (searchParam.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(searchParam.getType()));
        }
        
        if (searchParam.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(searchParam.getStatus()));
        }
        
        if (searchParam.getGroups() != null) {
        for (java.lang.String __item : searchParam.getGroups()) {
        __requestBuilder.addQueryParameter("groups", __queryParamTranscoder.encode(__item));
        }
        }
        
        if (searchParam.getTags() != null) {
        for (java.lang.String __item : searchParam.getTags()) {
        __requestBuilder.addQueryParameter("tags", __queryParamTranscoder.encode(__item));
        }
        }
        
        if (searchParam.getDefinitionIds() != null) {
        for (com.junbo.common.id.EntitlementDefinitionId __item : searchParam.getDefinitionIds()) {
        __requestBuilder.addQueryParameter("definitionIds", __queryParamTranscoder.encode(__item));
        }
        }
        
        if (searchParam.getStartGrantTime() != null) {
            __requestBuilder.addQueryParameter("startGrantTime", __queryParamTranscoder.encode(searchParam.getStartGrantTime()));
        }
        
        if (searchParam.getEndGrantTime() != null) {
            __requestBuilder.addQueryParameter("endGrantTime", __queryParamTranscoder.encode(searchParam.getEndGrantTime()));
        }
        
        if (searchParam.getStartExpirationTime() != null) {
            __requestBuilder.addQueryParameter("startExpirationTime", __queryParamTranscoder.encode(searchParam.getStartExpirationTime()));
        }
        
        if (searchParam.getEndExpirationTime() != null) {
            __requestBuilder.addQueryParameter("endExpirationTime", __queryParamTranscoder.encode(searchParam.getEndExpirationTime()));
        }
        
        if (searchParam.getLastModifiedTime() != null) {
            __requestBuilder.addQueryParameter("lastModifiedTime", __queryParamTranscoder.encode(searchParam.getLastModifiedTime()));
        }
            
        
        if (pageMetadata.getStart() != null) {
            __requestBuilder.addQueryParameter("start", __queryParamTranscoder.encode(pageMetadata.getStart()));
        }
        
        if (pageMetadata.getCount() != null) {
            __requestBuilder.addQueryParameter("count", __queryParamTranscoder.encode(pageMetadata.getCount()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement>>>() {
            @Override
            public Promise<com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement>>decode(new TypeReference<com.junbo.common.model.Results<com.junbo.entitlement.spec.model.Entitlement>>() {}, response.getResponseBody()));
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
