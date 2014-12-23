/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.handler

import com.junbo.langur.processor.writer.IncludeModelDirective
import com.junbo.langur.processor.writer.ModelWriterImpl
import freemarker.template.Configuration
import groovy.transform.CompileStatic

import javax.lang.model.element.TypeElement
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class ClientProxyGenerator implements RestResourceHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def clientProxy = handlerContext.clientProxy
        assert clientProxy != null, 'clientProxy is null'

        def filer = handlerContext.processingEnv.filer
        def sourceFile = filer.createSourceFile(clientProxy.packageName + '.' + clientProxy.className)

        Configuration freemarkerConfig = new Configuration()
        freemarkerConfig.setSharedVariable('includeModel', new IncludeModelDirective())
        freemarkerConfig.setClassForTemplateLoading(ClientProxyGenerator, '/')
        freemarkerConfig.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX)
        freemarkerConfig.setDefaultEncoding('utf-8')
        freemarkerConfig.setWhitespaceStripping(true)

        sourceFile.openWriter().withWriter { Writer out ->
            new ModelWriterImpl(freemarkerConfig).write(clientProxy, out)
        }
    }
}
