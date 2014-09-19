[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.ClientProxyModel" --]

// CHECKSTYLE:OFF

package ${packageName};

import com.junbo.langur.core.client.AbstractClientProxy;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.Router;
import com.junbo.langur.core.async.JunboAsyncHttpClient;
import com.junbo.langur.core.context.JunboHttpContext;
import com.ning.http.client.Response;

import javax.ws.rs.core.UriBuilder;

import static com.ning.http.client.extra.ListenableFutureAdapter.asGuavaFuture;

@org.springframework.stereotype.Component
public class ${className} extends AbstractClientProxy implements ${interfaceType},
    org.springframework.context.ApplicationContextAware {

    private org.springframework.context.ApplicationContext __applicationContext;

    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws org.springframework.beans.BeansException {
        __applicationContext = applicationContext;
    }

    private volatile ${interfaceType} __service;

    private boolean __serviceChecked;

    private ${interfaceType} __checkService() {
        if (__serviceChecked) {
            return __service;
        }

        synchronized(this) {
            if (__serviceChecked) {
                return __service;
            }

            if (__applicationContext.containsBean("default${interfaceSimpleType}")) {
                __service = __applicationContext.getBean("default${interfaceSimpleType}", ${interfaceType}.class);
            }

            __serviceChecked = true;
            return __service;
        }
    }

    [#list clientMethods as clientMethod]
        [@includeModel model=clientMethod indent=true/]

    [/#list]
}
