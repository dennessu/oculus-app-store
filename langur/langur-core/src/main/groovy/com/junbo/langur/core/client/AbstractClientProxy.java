// CHECKSTYLE:OFF
package com.junbo.langur.core.client;

import com.google.common.collect.HashMultimap;
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.context.JunboHttpContextScopeListener;
import com.ning.http.client.AsyncHttpClient;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * AbstractClientProxy.
 */
@CompileStatic
public abstract class AbstractClientProxy {
    protected AsyncHttpClient __client;

    protected String __target;

    protected MultivaluedMap<String, String> __headers = new MultivaluedHashMap<String, String>();

    protected MessageTranscoder __transcoder;

    protected PathParamTranscoder __pathParamTranscoder;

    protected QueryParamTranscoder __queryParamTranscoder;

    protected ExceptionHandler __exceptionHandler;

    protected AccessTokenProvider __accessTokenProvider;

    protected Executor __executor;

    protected boolean __inProcessCallable;

    @Autowired(required = false)
    protected List<JunboHttpContextScopeListener> __junboHttpContextScopeListeners;

    @Required
    public void setClient(AsyncHttpClient __client) {
        this.__client = __client;
    }

    @Required
    public void setTarget(String __target) {
        this.__target = __target;
    }

    public void setHeaders(MultivaluedMap<String, String> __headers) {
        this.__headers = __headers;
    }

    @Required
    public void setTranscoder(MessageTranscoder __transcoder) {
        this.__transcoder = __transcoder;
    }

    @Required
    public void setPathParamTranscoder(PathParamTranscoder __pathParamTranscoder) {
        this.__pathParamTranscoder = __pathParamTranscoder;
    }

    @Required
    public void setQueryParamTranscoder(QueryParamTranscoder __queryParamTranscoder) {
        this.__queryParamTranscoder = __queryParamTranscoder;
    }

    @Required
    public void setExceptionHandler(ExceptionHandler __exceptionHandler) {
        this.__exceptionHandler = __exceptionHandler;
    }

    public void setAccessTokenProvider(AccessTokenProvider __accessTokenProvider) {
        this.__accessTokenProvider = __accessTokenProvider;
    }

    @Required
    public void setExecutor(Executor __executor) {
        this.__executor = __executor;
    }

    public void setInProcessCallable(boolean inProcessCallable) {
        this.__inProcessCallable = inProcessCallable;
    }

    public void setJunboHttpContextScopeListeners(List<JunboHttpContextScopeListener> __junboHttpContextScopeListeners) {
        this.__junboHttpContextScopeListeners = __junboHttpContextScopeListeners;
    }

    protected JunboHttpContext.JunboHttpContextData __createJunboHttpContextData(com.ning.http.client.Request request) {
        JunboHttpContext.JunboHttpContextData httpContextData = new JunboHttpContext.JunboHttpContextData();

        httpContextData.setRequestMethod(request.getMethod());
        httpContextData.setRequestUri(request.getOriginalURI());

        for (Map.Entry<String, List<String>> entry : request.getHeaders()) {
            httpContextData.getRequestHeaders().addAll(entry.getKey(), entry.getValue());
        }

        httpContextData.setRequestIpAddress("0.0.0.0");

        return httpContextData;
    }
}
