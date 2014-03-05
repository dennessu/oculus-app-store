[#-- @ftlvariable name="" type="com.goshop.langur.processor.model.ClientProxyModel" --]

package ${packageName};

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.base.Function;

import com.goshop.langur.core.client.ClientResponseException;
import com.goshop.langur.core.client.MessageTranscoder;
import com.goshop.langur.core.client.TypeReference;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

@org.springframework.stereotype.Component
public class ${className} implements ${interfaceType} {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    public ${className}(AsyncHttpClient client, MessageTranscoder transcoder, String target) {
        this(client, transcoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public ${className}(AsyncHttpClient client, MessageTranscoder transcoder, String target, MultivaluedMap<String, Object> headers) {
        assert client != null : "client is null";
        assert transcoder != null : "transcoder is null";
        assert target != null : "target is null";
        assert headers != null : "headers is null";

        __client = client;
        __transcoder = transcoder;
        __target = target;
        __headers = headers;
    }

    [#list clientMethods as clientMethod]
        [@includeModel model=clientMethod indent=true/]

    [/#list]
}
