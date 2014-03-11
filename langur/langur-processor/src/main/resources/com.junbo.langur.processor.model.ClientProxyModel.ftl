[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientProxyModel" --]

// CHECKSTYLE:OFF

package ${packageName};

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
import com.junbo.langur.core.client.PathParamTranscoder;
import com.junbo.langur.core.client.TypeReference;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

@org.springframework.stereotype.Component
public class ${className} implements ${interfaceType} {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    private final PathParamTranscoder __pathParamTranscoder;

    public ${className}(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder, String target) {
        this(client, transcoder, pathParamTranscoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public ${className}(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder, String target, MultivaluedMap<String, Object> headers) {
        assert client != null : "client is null";
        assert transcoder != null : "transcoder is null";
        assert pathParamTranscoder != null : "pathParamTranscoder is null";
        assert target != null : "target is null";
        assert headers != null : "headers is null";

        __client = client;
        __transcoder = transcoder;
        __pathParamTranscoder = pathParamTranscoder;
        __target = target;
        __headers = headers;
    }

    [#list clientMethods as clientMethod]
        [@includeModel model=clientMethod indent=true/]

    [/#list]
}
