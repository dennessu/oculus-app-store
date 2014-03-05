/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor

import com.junbo.oom.processor.factory.MappingMethodRefResolverImpl
import com.junbo.oom.processor.factory.PropertyFactoryImpl
import com.junbo.oom.processor.factory.TypeFactoryImpl
import com.junbo.oom.processor.handler.HandlerContext
import com.junbo.oom.processor.handler.MapperHandler
import groovy.transform.CompileStatic

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
/**
 * Java doc.
 */
@SupportedAnnotationTypes('com.junbo.oom.core.Mapper')
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@CompileStatic
class MappingProcessor extends AbstractProcessor {

    @Override
    boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true
        }

        try {
            def handlers = findHandlers()

            annotations.each { TypeElement annotation ->
                roundEnv.getElementsAnnotatedWith(annotation).each { TypeElement typeElement ->
                    if (typeElement.kind != ElementKind.INTERFACE) {
                        throw new ProcessingException(
                                'com.junbo.oom.core.Mapper should be applied to interface only.', typeElement)
                    }

                    def typeFactory = new TypeFactoryImpl(processingEnv)
                    def propertyFactory = new PropertyFactoryImpl(processingEnv, typeFactory)
                    def mappingMethodRefResolver = new MappingMethodRefResolverImpl()

                    HandlerContext handlerContext = new HandlerContext(
                            processingEnv:processingEnv,
                            typeFactory:typeFactory,
                            propertyFactory:propertyFactory,
                            mappingMethodRefResolver:mappingMethodRefResolver
                    )

                    handlers.each { MapperHandler handler ->
                        handler.handle(typeElement, handlerContext)
                    }
                }
            }

            return true
        } catch (ProcessingException ex) {
            def message = "$ex"
            if (ex.element != null) {
                message += "\nElement: $ex.element"
            }

            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, ex.element)
            return false
        }
    }

    private static List<MapperHandler> findHandlers() {
        return ServiceLoader.load(MapperHandler, MappingProcessor.classLoader).
          iterator().toList().sort {
            MapperHandler o1, MapperHandler o2 -> o1.sequenceNumber - o2.sequenceNumber
        }
    }
}

