
// CHECKSTYLE:OFF

package com.junbo.langur.processor.test.proxy;

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
public class CategoryResourceClientProxy implements com.junbo.langur.processor.test.CategoryResource {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    private final PathParamTranscoder __pathParamTranscoder;

    private final QueryParamTranscoder __queryParamTranscoder;

    public CategoryResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
                                QueryParamTranscoder queryParamTranscoder, String target) {
        this(client, transcoder, pathParamTranscoder, queryParamTranscoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public CategoryResourceClientProxy(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
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

    
    public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> getCategories(com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        
        if (getOptions.getName() != null) {
            __requestBuilder.addQueryParameter("name", __queryParamTranscoder.encode(getOptions.getName()));
        }
        
        if (getOptions.getTestBoolean() != null) {
            __requestBuilder.addQueryParameter("testBoolean", __queryParamTranscoder.encode(getOptions.getTestBoolean()));
        }
        
        if (getOptions.getTestPrimaryBoolean() != null) {
            __requestBuilder.addQueryParameter("testPrimaryBoolean", __queryParamTranscoder.encode(getOptions.getTestPrimaryBoolean()));
        }
        
        if (getOptions.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(getOptions.getType()));
        }
        
        if (getOptions.getParentCategoryId() != null) {
            __requestBuilder.addQueryParameter("parentCategoryId", __queryParamTranscoder.encode(getOptions.getParentCategoryId()));
        }
        
        if (getOptions.getCategoryId() != null) {
            __requestBuilder.addQueryParameter("categoryId", __queryParamTranscoder.encode(getOptions.getCategoryId()));
        }
        
        if (getOptions.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(getOptions.getStatus()));
        }
        
        if (getOptions.getRevision() != null) {
            __requestBuilder.addQueryParameter("revision", __queryParamTranscoder.encode(getOptions.getRevision()));
        }
        
        if (getOptions.getPage() != null) {
            __requestBuilder.addQueryParameter("page", __queryParamTranscoder.encode(getOptions.getPage()));
        }
        
        if (getOptions.getSize() != null) {
            __requestBuilder.addQueryParameter("size", __queryParamTranscoder.encode(getOptions.getSize()));
        }
        
        if (getOptions.getFields() != null) {
            __requestBuilder.addQueryParameter("fields", __queryParamTranscoder.encode(getOptions.getFields()));
        }
        
        if (getOptions.getOrder() != null) {
            __requestBuilder.addQueryParameter("order", __queryParamTranscoder.encode(getOptions.getOrder()));
        }
        
        if (getOptions.getExpand() != null) {
            __requestBuilder.addQueryParameter("expand", __queryParamTranscoder.encode(getOptions.getExpand()));
        }
        
        if (getOptions.getLocale() != null) {
            __requestBuilder.addQueryParameter("locale", __queryParamTranscoder.encode(getOptions.getLocale()));
        }
        
        if (getOptions.getCountry() != null) {
            __requestBuilder.addQueryParameter("country", __queryParamTranscoder.encode(getOptions.getCountry()));
        }
        
        if (getOptions.getCurrency() != null) {
            __requestBuilder.addQueryParameter("currency", __queryParamTranscoder.encode(getOptions.getCurrency()));
        }
        
        if (getOptions.getModifiedSince() != null) {
            __requestBuilder.addQueryParameter("modifiedSince", __queryParamTranscoder.encode(getOptions.getModifiedSince()));
        }
        
        if (getOptions.getModifiedUntil() != null) {
            __requestBuilder.addQueryParameter("modifiedUntil", __queryParamTranscoder.encode(getOptions.getModifiedUntil()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>decode(new TypeReference<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.category.Category> getCategory(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        if (getOptions.getName() != null) {
            __requestBuilder.addQueryParameter("name", __queryParamTranscoder.encode(getOptions.getName()));
        }
        
        if (getOptions.getTestBoolean() != null) {
            __requestBuilder.addQueryParameter("testBoolean", __queryParamTranscoder.encode(getOptions.getTestBoolean()));
        }
        
        if (getOptions.getTestPrimaryBoolean() != null) {
            __requestBuilder.addQueryParameter("testPrimaryBoolean", __queryParamTranscoder.encode(getOptions.getTestPrimaryBoolean()));
        }
        
        if (getOptions.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(getOptions.getType()));
        }
        
        if (getOptions.getParentCategoryId() != null) {
            __requestBuilder.addQueryParameter("parentCategoryId", __queryParamTranscoder.encode(getOptions.getParentCategoryId()));
        }
        
        if (getOptions.getCategoryId() != null) {
            __requestBuilder.addQueryParameter("categoryId", __queryParamTranscoder.encode(getOptions.getCategoryId()));
        }
        
        if (getOptions.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(getOptions.getStatus()));
        }
        
        if (getOptions.getRevision() != null) {
            __requestBuilder.addQueryParameter("revision", __queryParamTranscoder.encode(getOptions.getRevision()));
        }
        
        if (getOptions.getPage() != null) {
            __requestBuilder.addQueryParameter("page", __queryParamTranscoder.encode(getOptions.getPage()));
        }
        
        if (getOptions.getSize() != null) {
            __requestBuilder.addQueryParameter("size", __queryParamTranscoder.encode(getOptions.getSize()));
        }
        
        if (getOptions.getFields() != null) {
            __requestBuilder.addQueryParameter("fields", __queryParamTranscoder.encode(getOptions.getFields()));
        }
        
        if (getOptions.getOrder() != null) {
            __requestBuilder.addQueryParameter("order", __queryParamTranscoder.encode(getOptions.getOrder()));
        }
        
        if (getOptions.getExpand() != null) {
            __requestBuilder.addQueryParameter("expand", __queryParamTranscoder.encode(getOptions.getExpand()));
        }
        
        if (getOptions.getLocale() != null) {
            __requestBuilder.addQueryParameter("locale", __queryParamTranscoder.encode(getOptions.getLocale()));
        }
        
        if (getOptions.getCountry() != null) {
            __requestBuilder.addQueryParameter("country", __queryParamTranscoder.encode(getOptions.getCountry()));
        }
        
        if (getOptions.getCurrency() != null) {
            __requestBuilder.addQueryParameter("currency", __queryParamTranscoder.encode(getOptions.getCurrency()));
        }
        
        if (getOptions.getModifiedSince() != null) {
            __requestBuilder.addQueryParameter("modifiedSince", __queryParamTranscoder.encode(getOptions.getModifiedSince()));
        }
        
        if (getOptions.getModifiedUntil() != null) {
            __requestBuilder.addQueryParameter("modifiedUntil", __queryParamTranscoder.encode(getOptions.getModifiedUntil()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.category.Category> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.category.Category>decode(new TypeReference<com.junbo.langur.processor.model.category.Category>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.category.Category> getCategoryDraft(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}/draft");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        if (getOptions.getName() != null) {
            __requestBuilder.addQueryParameter("name", __queryParamTranscoder.encode(getOptions.getName()));
        }
        
        if (getOptions.getTestBoolean() != null) {
            __requestBuilder.addQueryParameter("testBoolean", __queryParamTranscoder.encode(getOptions.getTestBoolean()));
        }
        
        if (getOptions.getTestPrimaryBoolean() != null) {
            __requestBuilder.addQueryParameter("testPrimaryBoolean", __queryParamTranscoder.encode(getOptions.getTestPrimaryBoolean()));
        }
        
        if (getOptions.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(getOptions.getType()));
        }
        
        if (getOptions.getParentCategoryId() != null) {
            __requestBuilder.addQueryParameter("parentCategoryId", __queryParamTranscoder.encode(getOptions.getParentCategoryId()));
        }
        
        if (getOptions.getCategoryId() != null) {
            __requestBuilder.addQueryParameter("categoryId", __queryParamTranscoder.encode(getOptions.getCategoryId()));
        }
        
        if (getOptions.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(getOptions.getStatus()));
        }
        
        if (getOptions.getRevision() != null) {
            __requestBuilder.addQueryParameter("revision", __queryParamTranscoder.encode(getOptions.getRevision()));
        }
        
        if (getOptions.getPage() != null) {
            __requestBuilder.addQueryParameter("page", __queryParamTranscoder.encode(getOptions.getPage()));
        }
        
        if (getOptions.getSize() != null) {
            __requestBuilder.addQueryParameter("size", __queryParamTranscoder.encode(getOptions.getSize()));
        }
        
        if (getOptions.getFields() != null) {
            __requestBuilder.addQueryParameter("fields", __queryParamTranscoder.encode(getOptions.getFields()));
        }
        
        if (getOptions.getOrder() != null) {
            __requestBuilder.addQueryParameter("order", __queryParamTranscoder.encode(getOptions.getOrder()));
        }
        
        if (getOptions.getExpand() != null) {
            __requestBuilder.addQueryParameter("expand", __queryParamTranscoder.encode(getOptions.getExpand()));
        }
        
        if (getOptions.getLocale() != null) {
            __requestBuilder.addQueryParameter("locale", __queryParamTranscoder.encode(getOptions.getLocale()));
        }
        
        if (getOptions.getCountry() != null) {
            __requestBuilder.addQueryParameter("country", __queryParamTranscoder.encode(getOptions.getCountry()));
        }
        
        if (getOptions.getCurrency() != null) {
            __requestBuilder.addQueryParameter("currency", __queryParamTranscoder.encode(getOptions.getCurrency()));
        }
        
        if (getOptions.getModifiedSince() != null) {
            __requestBuilder.addQueryParameter("modifiedSince", __queryParamTranscoder.encode(getOptions.getModifiedSince()));
        }
        
        if (getOptions.getModifiedUntil() != null) {
            __requestBuilder.addQueryParameter("modifiedUntil", __queryParamTranscoder.encode(getOptions.getModifiedUntil()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.category.Category> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.category.Category>decode(new TypeReference<com.junbo.langur.processor.model.category.Category>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> getCategoryChildren(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}/children");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        if (getOptions.getName() != null) {
            __requestBuilder.addQueryParameter("name", __queryParamTranscoder.encode(getOptions.getName()));
        }
        
        if (getOptions.getTestBoolean() != null) {
            __requestBuilder.addQueryParameter("testBoolean", __queryParamTranscoder.encode(getOptions.getTestBoolean()));
        }
        
        if (getOptions.getTestPrimaryBoolean() != null) {
            __requestBuilder.addQueryParameter("testPrimaryBoolean", __queryParamTranscoder.encode(getOptions.getTestPrimaryBoolean()));
        }
        
        if (getOptions.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(getOptions.getType()));
        }
        
        if (getOptions.getParentCategoryId() != null) {
            __requestBuilder.addQueryParameter("parentCategoryId", __queryParamTranscoder.encode(getOptions.getParentCategoryId()));
        }
        
        if (getOptions.getCategoryId() != null) {
            __requestBuilder.addQueryParameter("categoryId", __queryParamTranscoder.encode(getOptions.getCategoryId()));
        }
        
        if (getOptions.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(getOptions.getStatus()));
        }
        
        if (getOptions.getRevision() != null) {
            __requestBuilder.addQueryParameter("revision", __queryParamTranscoder.encode(getOptions.getRevision()));
        }
        
        if (getOptions.getPage() != null) {
            __requestBuilder.addQueryParameter("page", __queryParamTranscoder.encode(getOptions.getPage()));
        }
        
        if (getOptions.getSize() != null) {
            __requestBuilder.addQueryParameter("size", __queryParamTranscoder.encode(getOptions.getSize()));
        }
        
        if (getOptions.getFields() != null) {
            __requestBuilder.addQueryParameter("fields", __queryParamTranscoder.encode(getOptions.getFields()));
        }
        
        if (getOptions.getOrder() != null) {
            __requestBuilder.addQueryParameter("order", __queryParamTranscoder.encode(getOptions.getOrder()));
        }
        
        if (getOptions.getExpand() != null) {
            __requestBuilder.addQueryParameter("expand", __queryParamTranscoder.encode(getOptions.getExpand()));
        }
        
        if (getOptions.getLocale() != null) {
            __requestBuilder.addQueryParameter("locale", __queryParamTranscoder.encode(getOptions.getLocale()));
        }
        
        if (getOptions.getCountry() != null) {
            __requestBuilder.addQueryParameter("country", __queryParamTranscoder.encode(getOptions.getCountry()));
        }
        
        if (getOptions.getCurrency() != null) {
            __requestBuilder.addQueryParameter("currency", __queryParamTranscoder.encode(getOptions.getCurrency()));
        }
        
        if (getOptions.getModifiedSince() != null) {
            __requestBuilder.addQueryParameter("modifiedSince", __queryParamTranscoder.encode(getOptions.getModifiedSince()));
        }
        
        if (getOptions.getModifiedUntil() != null) {
            __requestBuilder.addQueryParameter("modifiedUntil", __queryParamTranscoder.encode(getOptions.getModifiedUntil()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>decode(new TypeReference<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> getCategoryDescendents(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}/descendents");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        if (getOptions.getName() != null) {
            __requestBuilder.addQueryParameter("name", __queryParamTranscoder.encode(getOptions.getName()));
        }
        
        if (getOptions.getTestBoolean() != null) {
            __requestBuilder.addQueryParameter("testBoolean", __queryParamTranscoder.encode(getOptions.getTestBoolean()));
        }
        
        if (getOptions.getTestPrimaryBoolean() != null) {
            __requestBuilder.addQueryParameter("testPrimaryBoolean", __queryParamTranscoder.encode(getOptions.getTestPrimaryBoolean()));
        }
        
        if (getOptions.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(getOptions.getType()));
        }
        
        if (getOptions.getParentCategoryId() != null) {
            __requestBuilder.addQueryParameter("parentCategoryId", __queryParamTranscoder.encode(getOptions.getParentCategoryId()));
        }
        
        if (getOptions.getCategoryId() != null) {
            __requestBuilder.addQueryParameter("categoryId", __queryParamTranscoder.encode(getOptions.getCategoryId()));
        }
        
        if (getOptions.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(getOptions.getStatus()));
        }
        
        if (getOptions.getRevision() != null) {
            __requestBuilder.addQueryParameter("revision", __queryParamTranscoder.encode(getOptions.getRevision()));
        }
        
        if (getOptions.getPage() != null) {
            __requestBuilder.addQueryParameter("page", __queryParamTranscoder.encode(getOptions.getPage()));
        }
        
        if (getOptions.getSize() != null) {
            __requestBuilder.addQueryParameter("size", __queryParamTranscoder.encode(getOptions.getSize()));
        }
        
        if (getOptions.getFields() != null) {
            __requestBuilder.addQueryParameter("fields", __queryParamTranscoder.encode(getOptions.getFields()));
        }
        
        if (getOptions.getOrder() != null) {
            __requestBuilder.addQueryParameter("order", __queryParamTranscoder.encode(getOptions.getOrder()));
        }
        
        if (getOptions.getExpand() != null) {
            __requestBuilder.addQueryParameter("expand", __queryParamTranscoder.encode(getOptions.getExpand()));
        }
        
        if (getOptions.getLocale() != null) {
            __requestBuilder.addQueryParameter("locale", __queryParamTranscoder.encode(getOptions.getLocale()));
        }
        
        if (getOptions.getCountry() != null) {
            __requestBuilder.addQueryParameter("country", __queryParamTranscoder.encode(getOptions.getCountry()));
        }
        
        if (getOptions.getCurrency() != null) {
            __requestBuilder.addQueryParameter("currency", __queryParamTranscoder.encode(getOptions.getCurrency()));
        }
        
        if (getOptions.getModifiedSince() != null) {
            __requestBuilder.addQueryParameter("modifiedSince", __queryParamTranscoder.encode(getOptions.getModifiedSince()));
        }
        
        if (getOptions.getModifiedUntil() != null) {
            __requestBuilder.addQueryParameter("modifiedUntil", __queryParamTranscoder.encode(getOptions.getModifiedUntil()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>decode(new TypeReference<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> getCategoryParents(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}/parents");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        if (getOptions.getName() != null) {
            __requestBuilder.addQueryParameter("name", __queryParamTranscoder.encode(getOptions.getName()));
        }
        
        if (getOptions.getTestBoolean() != null) {
            __requestBuilder.addQueryParameter("testBoolean", __queryParamTranscoder.encode(getOptions.getTestBoolean()));
        }
        
        if (getOptions.getTestPrimaryBoolean() != null) {
            __requestBuilder.addQueryParameter("testPrimaryBoolean", __queryParamTranscoder.encode(getOptions.getTestPrimaryBoolean()));
        }
        
        if (getOptions.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(getOptions.getType()));
        }
        
        if (getOptions.getParentCategoryId() != null) {
            __requestBuilder.addQueryParameter("parentCategoryId", __queryParamTranscoder.encode(getOptions.getParentCategoryId()));
        }
        
        if (getOptions.getCategoryId() != null) {
            __requestBuilder.addQueryParameter("categoryId", __queryParamTranscoder.encode(getOptions.getCategoryId()));
        }
        
        if (getOptions.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(getOptions.getStatus()));
        }
        
        if (getOptions.getRevision() != null) {
            __requestBuilder.addQueryParameter("revision", __queryParamTranscoder.encode(getOptions.getRevision()));
        }
        
        if (getOptions.getPage() != null) {
            __requestBuilder.addQueryParameter("page", __queryParamTranscoder.encode(getOptions.getPage()));
        }
        
        if (getOptions.getSize() != null) {
            __requestBuilder.addQueryParameter("size", __queryParamTranscoder.encode(getOptions.getSize()));
        }
        
        if (getOptions.getFields() != null) {
            __requestBuilder.addQueryParameter("fields", __queryParamTranscoder.encode(getOptions.getFields()));
        }
        
        if (getOptions.getOrder() != null) {
            __requestBuilder.addQueryParameter("order", __queryParamTranscoder.encode(getOptions.getOrder()));
        }
        
        if (getOptions.getExpand() != null) {
            __requestBuilder.addQueryParameter("expand", __queryParamTranscoder.encode(getOptions.getExpand()));
        }
        
        if (getOptions.getLocale() != null) {
            __requestBuilder.addQueryParameter("locale", __queryParamTranscoder.encode(getOptions.getLocale()));
        }
        
        if (getOptions.getCountry() != null) {
            __requestBuilder.addQueryParameter("country", __queryParamTranscoder.encode(getOptions.getCountry()));
        }
        
        if (getOptions.getCurrency() != null) {
            __requestBuilder.addQueryParameter("currency", __queryParamTranscoder.encode(getOptions.getCurrency()));
        }
        
        if (getOptions.getModifiedSince() != null) {
            __requestBuilder.addQueryParameter("modifiedSince", __queryParamTranscoder.encode(getOptions.getModifiedSince()));
        }
        
        if (getOptions.getModifiedUntil() != null) {
            __requestBuilder.addQueryParameter("modifiedUntil", __queryParamTranscoder.encode(getOptions.getModifiedUntil()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>decode(new TypeReference<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> getCategoryHists(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryGetOptions getOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}/hists");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.prepareGet("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        if (getOptions.getName() != null) {
            __requestBuilder.addQueryParameter("name", __queryParamTranscoder.encode(getOptions.getName()));
        }
        
        if (getOptions.getTestBoolean() != null) {
            __requestBuilder.addQueryParameter("testBoolean", __queryParamTranscoder.encode(getOptions.getTestBoolean()));
        }
        
        if (getOptions.getTestPrimaryBoolean() != null) {
            __requestBuilder.addQueryParameter("testPrimaryBoolean", __queryParamTranscoder.encode(getOptions.getTestPrimaryBoolean()));
        }
        
        if (getOptions.getType() != null) {
            __requestBuilder.addQueryParameter("type", __queryParamTranscoder.encode(getOptions.getType()));
        }
        
        if (getOptions.getParentCategoryId() != null) {
            __requestBuilder.addQueryParameter("parentCategoryId", __queryParamTranscoder.encode(getOptions.getParentCategoryId()));
        }
        
        if (getOptions.getCategoryId() != null) {
            __requestBuilder.addQueryParameter("categoryId", __queryParamTranscoder.encode(getOptions.getCategoryId()));
        }
        
        if (getOptions.getStatus() != null) {
            __requestBuilder.addQueryParameter("status", __queryParamTranscoder.encode(getOptions.getStatus()));
        }
        
        if (getOptions.getRevision() != null) {
            __requestBuilder.addQueryParameter("revision", __queryParamTranscoder.encode(getOptions.getRevision()));
        }
        
        if (getOptions.getPage() != null) {
            __requestBuilder.addQueryParameter("page", __queryParamTranscoder.encode(getOptions.getPage()));
        }
        
        if (getOptions.getSize() != null) {
            __requestBuilder.addQueryParameter("size", __queryParamTranscoder.encode(getOptions.getSize()));
        }
        
        if (getOptions.getFields() != null) {
            __requestBuilder.addQueryParameter("fields", __queryParamTranscoder.encode(getOptions.getFields()));
        }
        
        if (getOptions.getOrder() != null) {
            __requestBuilder.addQueryParameter("order", __queryParamTranscoder.encode(getOptions.getOrder()));
        }
        
        if (getOptions.getExpand() != null) {
            __requestBuilder.addQueryParameter("expand", __queryParamTranscoder.encode(getOptions.getExpand()));
        }
        
        if (getOptions.getLocale() != null) {
            __requestBuilder.addQueryParameter("locale", __queryParamTranscoder.encode(getOptions.getLocale()));
        }
        
        if (getOptions.getCountry() != null) {
            __requestBuilder.addQueryParameter("country", __queryParamTranscoder.encode(getOptions.getCountry()));
        }
        
        if (getOptions.getCurrency() != null) {
            __requestBuilder.addQueryParameter("currency", __queryParamTranscoder.encode(getOptions.getCurrency()));
        }
        
        if (getOptions.getModifiedSince() != null) {
            __requestBuilder.addQueryParameter("modifiedSince", __queryParamTranscoder.encode(getOptions.getModifiedSince()));
        }
        
        if (getOptions.getModifiedUntil() != null) {
            __requestBuilder.addQueryParameter("modifiedUntil", __queryParamTranscoder.encode(getOptions.getModifiedUntil()));
        }
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>decode(new TypeReference<com.junbo.langur.processor.model.ResultList<com.junbo.langur.processor.model.category.Category>>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.category.Category> createCategory(com.junbo.langur.processor.model.category.Category category, com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __requestBuilder.setBody(__transcoder.encode(category));
            
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.category.Category> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.category.Category>decode(new TypeReference<com.junbo.langur.processor.model.category.Category>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.category.Category> updateCategory(java.lang.String categoryId, com.junbo.langur.processor.model.category.Category category, com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        __requestBuilder.setBody(__transcoder.encode(category));
            
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.category.Category> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.category.Category>decode(new TypeReference<com.junbo.langur.processor.model.category.Category>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.category.Category> releaseCategory(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}/release");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.category.Category> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.category.Category>decode(new TypeReference<com.junbo.langur.processor.model.category.Category>() {}, response.getResponseBody()));
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
    
    
    public Promise<com.junbo.langur.processor.model.category.Category> deleteCategory(java.lang.String categoryId, com.junbo.langur.processor.model.options.category.CategoryPostOptions postOptions) {
    
        javax.ws.rs.core.UriBuilder __uriBuilder = UriBuilder.fromUri(__target);
        __uriBuilder.path("categories/{categoryId}/delete");
    
        AsyncHttpClient.BoundRequestBuilder __requestBuilder = __client.preparePost("http://127.0.0.1"); // the url will be overwritten later
    
        for (java.util.Map.Entry<String, java.util.List<Object>> entry : __headers.entrySet()) {
            for (Object value : entry.getValue()) {
                __requestBuilder.addHeader(entry.getKey(), value.toString());
            }
        }
    
        __requestBuilder.addHeader("Accept", "application/json");
    
        __requestBuilder.addHeader("Content-Type", "application/json");
    
        
        __uriBuilder.resolveTemplate("categoryId", __pathParamTranscoder.encode(categoryId));
            
        
        __requestBuilder.setUrl(__uriBuilder.toTemplate());
    
        Promise<Response> __future;
    
        try {
            __future = Promise.wrap(asGuavaFuture(__requestBuilder.execute()));
        } catch (java.io.IOException ex) {
            throw new RuntimeException(ex);
        }
    
        return __future.then(new Promise.Func<Response, Promise<com.junbo.langur.processor.model.category.Category>>() {
            @Override
            public Promise<com.junbo.langur.processor.model.category.Category> apply(Response response) {
                if (response.getStatusCode() / 100 == 2) {
                    try {
                        return Promise.pure(__transcoder.<com.junbo.langur.processor.model.category.Category>decode(new TypeReference<com.junbo.langur.processor.model.category.Category>() {}, response.getResponseBody()));
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
