/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor

import com.junbo.langur.processor.handler.HandlerContext
import com.junbo.langur.processor.handler.RestResourceHandler
import groovy.transform.CompileStatic

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
abstract class AbstractRestResourceProcessor extends AbstractProcessor {
    
    protected abstract List<RestResourceHandler> findRestResourceHandlers()

    @Override
    boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false
        }

        try {
            annotations.each { TypeElement annotation ->
                roundEnv.getElementsAnnotatedWith(annotation).each { TypeElement typeElement ->
                    if (typeElement.kind != ElementKind.INTERFACE) {
                        throw new ProcessingException(
                                'com.junbo.langur.core.RestResource should be applied to interface only.', typeElement)
                    }

                    def context = new HandlerContext(
                        processingEnv:processingEnv
                    )

                    findRestResourceHandlers().each {
                        RestResourceHandler handler -> handler.handle(typeElement, context)
                    }
                }
            }

            false
        } catch (ProcessingException ex) {
            def message = "$ex"
            if (ex.element != null) {
                message += "\nElement: $ex.element"
            }

            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, ex.element)
            false
        }
    }
}

