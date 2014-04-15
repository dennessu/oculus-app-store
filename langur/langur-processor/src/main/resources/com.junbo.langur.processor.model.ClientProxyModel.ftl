[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientProxyModel" --]

// CHECKSTYLE:OFF

package ${packageName};

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
public class ${className} implements ${interfaceType} {

    private final AsyncHttpClient __client;

    private final String __target;

    private final MultivaluedMap<String, Object> __headers;

    private final MessageTranscoder __transcoder;

    private final PathParamTranscoder __pathParamTranscoder;

    private final QueryParamTranscoder __queryParamTranscoder;

    public ${className}(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
                                QueryParamTranscoder queryParamTranscoder, String target) {
        this(client, transcoder, pathParamTranscoder, queryParamTranscoder, target, new javax.ws.rs.core.MultivaluedHashMap<String, Object>());
    }

    public ${className}(AsyncHttpClient client, MessageTranscoder transcoder, PathParamTranscoder pathParamTranscoder,
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

    [#list clientMethods as clientMethod]
        [@includeModel model=clientMethod indent=true/]

    [/#list]

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

            @Override
            public Error error() {
                return error;
            }
        };
    }
}
