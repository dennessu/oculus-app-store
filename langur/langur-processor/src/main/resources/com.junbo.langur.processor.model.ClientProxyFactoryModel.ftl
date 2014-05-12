[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientProxyFactoryModel" --]

// CHECKSTYLE:OFF

package ${packageName};

import com.junbo.langur.core.client.*;
import com.ning.http.client.AsyncHttpClient;

import ${resourceType};

public class ${className} implements ClientProxyFactory<${resourceName}> {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("routingAsyncHttpClient")
    private AsyncHttpClient __client;

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

    public ${className}() { }

    public ${className}(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
            QueryParamTranscoder queryParamTranscoder, ExceptionHandler exceptionHandler,
            HeadersProvider headersProvider) {
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
    }

    public ${resourceName} create(String targetUrl) {
        return new ${resourceName}ClientProxy(__client, __transcoder, __pathParamTranscoder, __queryParamTranscoder, __exceptionHandler, targetUrl, __headersProvider.getHeaders());
    }
}
