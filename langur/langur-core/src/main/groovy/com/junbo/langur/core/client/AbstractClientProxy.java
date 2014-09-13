// CHECKSTYLE:OFF
package com.junbo.langur.core.client;

import com.junbo.langur.core.async.JunboAsyncHttpClient;
import com.junbo.langur.core.context.JunboHttpContext;
import com.junbo.langur.core.context.JunboHttpContextScopeListeners;
import com.junbo.langur.core.routing.Router;
import groovy.transform.CompileStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;

/**
 * AbstractClientProxy.
 */
@CompileStatic
public abstract class AbstractClientProxy {
    protected JunboAsyncHttpClient __client;

    protected String __target;

    protected MultivaluedMap<String, String> __headers = new MultivaluedHashMap<String, String>();

    protected MessageTranscoder __transcoder;

    protected PathParamTranscoder __pathParamTranscoder;

    protected QueryParamTranscoder __queryParamTranscoder;

    protected ExceptionHandler __exceptionHandler;

    protected HeadersProvider __headersProvider;

    protected ResponseHandler __responseHandler;

    protected AccessTokenProvider __accessTokenProvider;

    protected boolean __inProcessCallable;

    @Autowired(required = false)
    @Qualifier("junboHttpContextScopeListeners")
    protected JunboHttpContextScopeListeners __junboHttpContextScopeListeners;

    @Autowired(required = false)
    @Qualifier("routingDefaultRouter")
    protected Router __router;

    @Required
    public void setClient(JunboAsyncHttpClient __client) {
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

    public void setHeadersProvider(HeadersProvider __headersProvider) {
        this.__headersProvider = __headersProvider;
    }

    public void setResponseHandler(ResponseHandler __responseHandler) {
        this.__responseHandler = __responseHandler;
    }

    public void setAccessTokenProvider(AccessTokenProvider __accessTokenProvider) {
        this.__accessTokenProvider = __accessTokenProvider;
    }

    public void setInProcessCallable(boolean inProcessCallable) {
        this.__inProcessCallable = inProcessCallable;
    }

    public void setJunboHttpContextScopeListeners(JunboHttpContextScopeListeners __junboHttpContextScopeListeners) {
        this.__junboHttpContextScopeListeners = __junboHttpContextScopeListeners;
    }

    public void setRouter(Router router) {
        this.__router = router;
    }

    protected JunboHttpContext.JunboHttpContextData __createJunboHttpContextData(com.ning.http.client.Request request, Class handler) {
        JunboHttpContext.JunboHttpContextData httpContextData = new JunboHttpContext.JunboHttpContextData();

        httpContextData.setRequestMethod(request.getMethod());
        httpContextData.setRequestUri(request.getOriginalURI());
        httpContextData.setRequestHandler(handler);

        for (Map.Entry<String, List<String>> entry : request.getHeaders()) {
            httpContextData.getRequestHeaders().addAll(entry.getKey(), entry.getValue());
        }

        httpContextData.setRequestIpAddress("0.0.0.0");

        return httpContextData;
    }

    protected void __addHeadersFromHeadersProvider(JunboAsyncHttpClient.BoundRequestBuilder requestBuilder) {
        if (__headersProvider != null) {
            for (java.util.Map.Entry<String, java.util.List<String>> entry : __headersProvider.getHeaders().entrySet()) {
                for (String value : entry.getValue()) {
                    requestBuilder.addHeader(entry.getKey(), value);
                }
            }
        }
    }
}
