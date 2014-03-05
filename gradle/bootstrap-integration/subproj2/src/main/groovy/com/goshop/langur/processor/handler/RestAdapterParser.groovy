package com.goshop.langur.processor.handler

import com.goshop.langur.processor.ProcessingException
import com.goshop.langur.processor.model.RestAdapterModel
import com.goshop.langur.processor.model.RestMethodModel
import com.goshop.langur.processor.model.RestParameterModel
import groovy.transform.CompileStatic

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.ElementFilter

@CompileStatic
class RestAdapterParser implements RestResourceHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def elementUtils = handlerContext.processingEnv.elementUtils

        def restAdapter = new RestAdapterModel()
        restAdapter.packageName = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + ".adapter"
        restAdapter.className = mapperType.simpleName.toString() + "RestAdapter"

        restAdapter.adapteeName = mapperType.simpleName.toString()
        restAdapter.adapteeType = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + "." + mapperType.simpleName.toString()

        restAdapter.annotations = mapperType.annotationMirrors.collect {
            AnnotationMirror annotationMirror -> annotationMirror.toString()
        }.findAll {
            String annotation -> annotation != "@com.goshop.langur.core.RestResource"
        }.toList()

        restAdapter.restMethods = []
        ElementFilter.methodsIn(elementUtils.getAllMembers(mapperType)).each {
            ExecutableElement executableElement ->
                if (executableElement.enclosingElement.toString() == "java.lang.Object") {
                    return

                }

                if (!(executableElement.returnType instanceof DeclaredType)) {
                    throw new ProcessingException("returnType $executableElement.returnType must be a DeclaredType.", executableElement)
                }

                def returnType = (DeclaredType) executableElement.returnType
                def returnTypeElement = returnType.asElement()
                if (returnTypeElement.toString() != "com.google.common.util.concurrent.ListenableFuture") {
                    throw new ProcessingException("returnType $executableElement.returnType must be a com.google.common.util.concurrent.ListenableFuture.", executableElement)
                }

                def restMethod = new RestMethodModel()

                restMethod.returnType = returnType.typeArguments[0].toString()
                restMethod.methodName = executableElement.simpleName.toString()
                restMethod.annotations = executableElement.annotationMirrors.collect {
                    AnnotationMirror annotationMirror -> annotationMirror.toString()
                }

                restMethod.parameters = executableElement.parameters.collect {
                    VariableElement variableElement ->
                        new RestParameterModel(
                                paramName: variableElement.toString(),
                                paramType: variableElement.asType().toString(),
                                annotations: variableElement.annotationMirrors.collect {
                                    AnnotationMirror annotationMirror -> annotationMirror.toString()
                                })
                }

                restAdapter.restMethods.add(restMethod)
        }

        handlerContext.restAdapter = restAdapter
    }
}
