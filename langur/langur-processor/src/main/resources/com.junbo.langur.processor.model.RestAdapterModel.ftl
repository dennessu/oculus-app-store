[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.RestAdapterModel" --]
// CHECKSTYLE:OFF
package ${packageName};

import com.junbo.langur.core.client.ClientProxyFactory;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.filter.AfterInvocationFilter;
import com.junbo.langur.core.routing.Router;
import com.junbo.langur.core.context.JunboHttpContextScopeListener;
import com.junbo.langur.core.context.JunboHttpContextScopeListeners;
import com.junbo.langur.core.track.TrackContextManager;
import com.junbo.langur.core.track.UserLogProcessor;
import java.util.List;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
[#list annotations as annotation]
${annotation}
[/#list]
public class ${className} extends com.junbo.langur.core.adapter.AbstractRestAdapter {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("default${adapteeName}")
    private ${adapteeType} __adaptee;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    @org.springframework.beans.factory.annotation.Qualifier("default${adapteeName}ClientFactory")
    private ClientProxyFactory<${adapteeType}> __clientFactory;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    @org.springframework.beans.factory.annotation.Qualifier("defaultAfterInvocationFilter")
    private AfterInvocationFilter __afterInvocationFilter;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("junboHttpContextScopeListeners")
    protected JunboHttpContextScopeListeners __junboHttpContextScopeListeners;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    @org.springframework.beans.factory.annotation.Qualifier("userLogProcessor")
    private UserLogProcessor __userLogProcessor;

    [#if !authorizationNotRequired]
    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.junbo.langur.core.rest.ResourceScopeValidator __resourceScopeValidator;
    [/#if]

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private com.junbo.langur.core.rest.ThrottleController __throttleController;

    public ${adapteeType} getAdaptee() {
        return __adaptee;
    }

    public void setAdaptee(${adapteeType} adaptee) {
        __adaptee = adaptee;
    }

    public ClientProxyFactory<${adapteeType}> getClientFactory() {
        return __clientFactory;
    }

    public void setClientFactory(ClientProxyFactory<${adapteeType}> clientFactory) {
        __clientFactory = clientFactory;
    }

    public AfterInvocationFilter getAfterInvocationFilter() {
        return __afterInvocationFilter;
    }

    public void setAfterInvocationFilter(AfterInvocationFilter afterInvocationFilter) {
        __afterInvocationFilter = afterInvocationFilter;
    }

    public void setUserLogProcessor(UserLogProcessor UserLogProcessor) {
        __userLogProcessor = UserLogProcessor;
    }

    [#list restMethods as restMethod]
        [@includeModel model=restMethod indent=true/]

    [/#list]
}
