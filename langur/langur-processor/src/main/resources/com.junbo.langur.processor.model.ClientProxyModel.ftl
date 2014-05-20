[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientProxyModel" --]

// CHECKSTYLE:OFF

package ${packageName};

import com.junbo.langur.core.client.*;
import com.junbo.langur.core.promise.Promise;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.springframework.beans.factory.annotation.Required;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.util.concurrent.Executor;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

@org.springframework.stereotype.Component
public class ${className} extends AbstractClientProxy implements ${interfaceType} {

    [#list clientMethods as clientMethod]
        [@includeModel model=clientMethod indent=true/]

    [/#list]
}
