/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor

import com.junbo.langur.processor.handler.ClientProxyFactoryGenerator
import com.junbo.langur.processor.handler.ClientProxyFactoryParser
import com.junbo.langur.processor.handler.ClientProxyGenerator
import com.junbo.langur.processor.handler.ClientProxyParser
import com.junbo.langur.processor.handler.RestResourceHandler
import com.junbo.langur.processor.handler.SyncWrapperGenerator
import com.junbo.langur.processor.handler.SyncWrapperParser
import groovy.transform.CompileStatic

import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion

/**
 * ClientProxyProcessor
 */
@SupportedAnnotationTypes('com.junbo.langur.core.RestResource')
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@CompileStatic
class ClientProxyProcessor extends AbstractRestResourceProcessor {

    @Override
    protected List<RestResourceHandler> findRestResourceHandlers() {
        return [new ClientProxyParser(), new ClientProxyGenerator(),
                new ClientProxyFactoryParser(), new ClientProxyFactoryGenerator(),
                new SyncWrapperParser(), new SyncWrapperGenerator()]
    }
}
