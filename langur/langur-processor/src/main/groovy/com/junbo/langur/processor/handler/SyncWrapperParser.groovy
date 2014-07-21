/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.handler
import com.junbo.langur.processor.ProcessingException
import com.junbo.langur.processor.model.SyncWrapperMethodModel
import com.junbo.langur.processor.model.SyncWrapperModel
import com.junbo.langur.processor.model.SyncWrapperParameterModel
import groovy.transform.CompileStatic

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class SyncWrapperParser implements RestResourceHandler {
    private static final String OBJECT_TYPE = 'java.lang.Object'
    private static final String DOT = '.'
    private static TypeMirror collectionType

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def elementUtils = handlerContext.processingEnv.elementUtils
        def typeUtils = handlerContext.processingEnv.typeUtils

        collectionType = elementUtils.getTypeElement(Collection.canonicalName).asType()
        collectionType = typeUtils.erasure(collectionType)

        def syncWrapper = new SyncWrapperModel()
        syncWrapper.packageName = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + '.proxy'
        syncWrapper.className = mapperType.simpleName.toString() + 'Sync'

        syncWrapper.interfaceType = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + DOT +
                mapperType.simpleName.toString()

        syncWrapper.clientMethods = []
        ElementFilter.methodsIn(elementUtils.getAllMembers(mapperType)).each {
            ExecutableElement executableElement ->
                if (executableElement.enclosingElement.toString() == OBJECT_TYPE) {
                    return
                }

                if (!(executableElement.returnType instanceof DeclaredType)) {
                    throw new ProcessingException("returnType $executableElement.returnType must be a DeclaredType.",
                            executableElement)
                }

                def returnType = (DeclaredType) executableElement.returnType
                def returnTypeElement = returnType.asElement()
                if (returnTypeElement.toString() != 'com.junbo.langur.core.promise.Promise') {
                    throw new ProcessingException
                    ("returnType $executableElement.returnType must be a com.junbo.langur.core.promise.Promise.",
                            executableElement)
                }

                def syncMethod = new SyncWrapperMethodModel()

                syncMethod.returnType = returnType.typeArguments[0].toString()
                syncMethod.methodName = executableElement.simpleName.toString()

                syncMethod.parameters = executableElement.parameters.collect {
                    VariableElement variableElement ->
                        new SyncWrapperParameterModel(
                                paramName:variableElement.toString(),
                                paramType:variableElement.asType().toString(),
                                annotations:variableElement.annotationMirrors.collect {
                                    AnnotationMirror annotationMirror -> annotationMirror.toString()
                                })
                }

                syncWrapper.clientMethods.add(syncMethod)
        }

        handlerContext.syncWrapper = syncWrapper
    }
}
