package com.goshop.langur.processor.handler

import com.goshop.langur.processor.writer.IncludeModelDirective
import com.goshop.langur.processor.writer.ModelWriterImpl
import freemarker.template.Configuration
import groovy.transform.CompileStatic

import javax.lang.model.element.TypeElement

@CompileStatic
class ClientProxyGenerator implements RestResourceHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def clientProxy = handlerContext.clientProxy;
        assert clientProxy != null, "clientProxy is null"

        def filer = handlerContext.processingEnv.filer
        def sourceFile = filer.createSourceFile(clientProxy.packageName + "." + clientProxy.className)

        Configuration freemarkerConfig = new Configuration()
        freemarkerConfig.setSharedVariable("includeModel", new IncludeModelDirective())
        freemarkerConfig.setClassForTemplateLoading(ClientProxyGenerator.class, "/")
        freemarkerConfig.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX)
        freemarkerConfig.setDefaultEncoding("utf-8")
        freemarkerConfig.setWhitespaceStripping(true)

        sourceFile.openWriter().withWriter { Writer out ->
            new ModelWriterImpl(freemarkerConfig).write(clientProxy, out)
        }
    }
}
