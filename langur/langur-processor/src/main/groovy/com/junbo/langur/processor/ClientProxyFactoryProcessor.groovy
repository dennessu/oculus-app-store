/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor

import com.junbo.langur.processor.handler.ClientProxyFactoryGenerator
import com.junbo.langur.processor.handler.ClientProxyFactoryParser
import com.junbo.langur.processor.handler.RestResourceHandler
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
class ClientProxyFactoryProcessor extends AbstractRestResourceProcessor {

    @Override
    protected List<RestResourceHandler> findRestResourceHandlers() {
        return [new ClientProxyFactoryParser(), new ClientProxyFactoryGenerator()]
    }
}
