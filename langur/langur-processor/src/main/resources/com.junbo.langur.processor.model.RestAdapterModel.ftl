[#-- @ftlvariable name="" type="com.junbo.langur.processor.model.RestAdapterModel" --]
// CHECKSTYLE:OFF
package ${packageName};

import com.junbo.langur.core.client.ClientProxyFactory;
import com.junbo.langur.core.routing.Router;

@org.springframework.stereotype.Component
@org.springframework.context.annotation.Scope("prototype")
[#list annotations as annotation]
${annotation}
[/#list]
public class ${className} {

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.beans.factory.annotation.Qualifier("default${adapteeName}")
    private ${adapteeType} __adaptee;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    @org.springframework.beans.factory.annotation.Qualifier("default${adapteeName}ClientFactory")
    private ClientProxyFactory<${adapteeType}> __clientFactory;

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    @org.springframework.beans.factory.annotation.Qualifier("default${adapteeName}Router")
    private Router __router;

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

    public Router getRouter() {
        return __router;
    }

    public void setRouter(Router router) {
        __router = router;
    }

    [#list restMethods as restMethod]
        [@includeModel model=restMethod indent=true/]

    [/#list]
}
