package com.goshop.langur.processor.handler

import com.goshop.langur.processor.writer.IncludeModelDirective
import com.goshop.langur.processor.writer.ModelWriterImpl
import freemarker.template.Configuration
import groovy.transform.CompileStatic

import javax.lang.model.element.TypeElement

@CompileStatic
class RestAdapterGenerator implements RestResourceHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def restAdapter = handlerContext.restAdapter
        assert restAdapter != null, "restAdapter is null"

        def filer = handlerContext.processingEnv.filer
        def sourceFile = filer.createSourceFile(restAdapter.packageName + "." + restAdapter.className)

        Configuration freemarkerConfig = new Configuration()
        freemarkerConfig.setSharedVariable("includeModel", new IncludeModelDirective())
        freemarkerConfig.setClassForTemplateLoading(RestAdapterGenerator.class, "/")
        freemarkerConfig.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX)
        freemarkerConfig.setDefaultEncoding("utf-8")
        freemarkerConfig.setWhitespaceStripping(true)

        sourceFile.openWriter().withWriter { Writer out ->
            new ModelWriterImpl(freemarkerConfig).write(restAdapter, out)
        }
    }
}
