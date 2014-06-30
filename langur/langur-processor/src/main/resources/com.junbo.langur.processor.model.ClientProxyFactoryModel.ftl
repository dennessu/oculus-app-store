[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientProxyFactoryModel" --]

// CHECKSTYLE:OFF

package ${packageName};

import com.junbo.langur.core.client.*;
import com.junbo.langur.core.async.JunboAsyncHttpClient;

import com.junbo.langur.core.context.JunboHttpContextScopeListener;
import java.util.List;

import ${resourceType};

public class ${className} implements ClientProxyFactory<${resourceName}> {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingAsyncHttpClient")
    private JunboAsyncHttpClient __client;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingTranscoder")
    private MessageTranscoder __transcoder;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingPathParamTranscoder")
    private PathParamTranscoder __pathParamTranscoder;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingQueryParamTranscoder")
    private QueryParamTranscoder __queryParamTranscoder;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingExceptionHandler")
    private ExceptionHandler __exceptionHandler;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingHeadersProvider")
    private HeadersProvider __headersProvider;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingResponseHandler")
    private ResponseHandler __responseHandler;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    protected List<JunboHttpContextScopeListener> __junboHttpContextScopeListeners;

    public ${className}() { }

    public ${className}(
            JunboAsyncHttpClient client,
            MessageTranscoder transcoder,
            PathParamTranscoder pathParamTranscoder,
            QueryParamTranscoder queryParamTranscoder,
            ExceptionHandler exceptionHandler,
            HeadersProvider headersProvider,
            ResponseHandler responseHandler) {
        assert client != null : "client is null";
        assert transcoder != null : "transcoder is null";
        assert pathParamTranscoder != null : "pathParamTranscoder is null";
        assert queryParamTranscoder != null : "queryParamTranscoder is null";
        assert exceptionHandler != null : "exceptionHandler is null";
        assert headersProvider != null : "headersProvider is null";

        __client = client;
        __transcoder = transcoder;
        __pathParamTranscoder = pathParamTranscoder;
        __queryParamTranscoder = queryParamTranscoder;
        __exceptionHandler = exceptionHandler;
        __headersProvider = headersProvider;
        __responseHandler = responseHandler;
    }

    public ${resourceName} create(String targetUrl) {
        ${resourceName}ClientProxy clientProxy = new ${resourceName}ClientProxy();

        clientProxy.setClient(__client);
        clientProxy.setTranscoder(__transcoder);
        clientProxy.setPathParamTranscoder(__pathParamTranscoder);
        clientProxy.setQueryParamTranscoder(__queryParamTranscoder);
        clientProxy.setExceptionHandler(__exceptionHandler);
        clientProxy.setTarget(targetUrl);
        clientProxy.setHeaders(__headersProvider.getHeaders());
        clientProxy.setResponseHandler(__responseHandler);
        clientProxy.setJunboHttpContextScopeListeners(__junboHttpContextScopeListeners);

        return clientProxy;
    }
}
