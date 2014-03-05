/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.handler

import com.junbo.oom.processor.writer.IncludeModelDirective
import com.junbo.oom.processor.writer.ModelWriterImpl
import freemarker.template.Configuration
import groovy.transform.CompileStatic

import javax.annotation.processing.Filer
import javax.lang.model.element.TypeElement
import javax.tools.JavaFileObject
/**
 * Java doc.
 */
@CompileStatic
class RenderMapper implements MapperHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {

        def mapperModel = handlerContext.mapperModel

        Filer filer = handlerContext.processingEnv.filer

        JavaFileObject sourceFile = filer.createSourceFile(mapperModel.filename)

        Configuration freemarkerConfig = new Configuration()
        freemarkerConfig.setSharedVariable('includeModel', new IncludeModelDirective())
        freemarkerConfig.setClassForTemplateLoading(RenderMapper, '/')
        freemarkerConfig.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX)
        freemarkerConfig.setDefaultEncoding('utf-8')
        freemarkerConfig.setWhitespaceStripping(true)

        sourceFile.openWriter().withWriter { Writer out ->
            new ModelWriterImpl(freemarkerConfig).write(mapperModel, out)
        }
    }

    final int sequenceNumber = 30
}
