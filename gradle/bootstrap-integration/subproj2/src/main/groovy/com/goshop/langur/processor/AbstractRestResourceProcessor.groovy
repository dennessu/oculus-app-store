package com.goshop.langur.processor

import com.goshop.langur.processor.handler.HandlerContext
import com.goshop.langur.processor.handler.RestAdapterGenerator
import com.goshop.langur.processor.handler.RestAdapterParser
import com.goshop.langur.processor.handler.RestResourceHandler
import groovy.transform.CompileStatic

import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedAnnotationTypes
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@CompileStatic
abstract class AbstractRestResourceProcessor extends AbstractProcessor {

    protected abstract List<RestResourceHandler> getRestResourceHandlers();

    @Override
    boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false
        }


        try {
            annotations.each { TypeElement annotation ->
                roundEnv.getElementsAnnotatedWith(annotation).each { TypeElement typeElement ->
                    if (typeElement.getKind() != ElementKind.INTERFACE) {
                        throw new ProcessingException("com.goshop.langur.core.RestResource should be applied to interface only.", typeElement)
                    }

                    def context = new HandlerContext(
                            processingEnv: processingEnv
                    )

                    getRestResourceHandlers().each {
                        RestResourceHandler handler -> handler.handle(typeElement, context)
                    }
                }
            }

            return false;
        } catch (ProcessingException ex) {
            def message = "$ex"
            if (ex.element != null) {
                message += "\nElement: $ex.element"
            }

            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message, ex.element)
            return false;
        }
    }
}
