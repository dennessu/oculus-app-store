/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.handler
import com.junbo.langur.processor.model.ClientProxyFactoryModel
import groovy.transform.CompileStatic

import javax.lang.model.element.TypeElement
/**
 * ClientProxyFactoryParser to parse ClientProxyFactory model on RestResource.
 */
@CompileStatic
class ClientProxyFactoryParser implements RestResourceHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def elementUtils = handlerContext.processingEnv.elementUtils

        def clientProxyFactory = new ClientProxyFactoryModel()
        clientProxyFactory.packageName = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + '.proxy'
        clientProxyFactory.className = mapperType.simpleName.toString() + 'ClientProxyFactory'
        clientProxyFactory.resourceName = mapperType.simpleName.toString()
        clientProxyFactory.resourceType = mapperType.qualifiedName.toString()

        handlerContext.clientProxyFactory = clientProxyFactory
    }
}
