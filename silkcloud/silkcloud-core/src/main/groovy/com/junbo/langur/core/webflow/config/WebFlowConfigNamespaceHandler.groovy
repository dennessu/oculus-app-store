package com.junbo.langur.core.webflow.config

import groovy.transform.CompileStatic
import org.springframework.beans.factory.xml.NamespaceHandlerSupport

/**
 * Created by Shenhua on 3/1/14.
 */
@CompileStatic
class WebFlowConfigNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    void init() {
        registerBeanDefinitionParser('flow', new FlowBeanDefinitionParser())
    }
}
