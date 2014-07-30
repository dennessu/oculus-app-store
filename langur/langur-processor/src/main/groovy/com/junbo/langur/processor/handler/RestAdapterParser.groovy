/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.handler

import com.junbo.langur.core.AuthorizationNotRequired
import com.junbo.langur.core.routing.RouteBy
import com.junbo.langur.processor.ProcessingException
import com.junbo.langur.processor.model.RestAdapterModel
import com.junbo.langur.processor.model.RestMethodModel
import com.junbo.langur.processor.model.RestParameterModel
import groovy.transform.CompileStatic

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.ElementFilter
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class RestAdapterParser implements RestResourceHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def elementUtils = handlerContext.processingEnv.elementUtils
        def restAdapter = new RestAdapterModel()
        restAdapter.packageName = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + '.adapter'
        restAdapter.className = mapperType.simpleName.toString() + 'RestAdapter'

        restAdapter.adapteeName = mapperType.simpleName.toString()
        restAdapter.adapteeType = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + '.' +
                mapperType.simpleName.toString()

        restAdapter.annotations = mapperType.annotationMirrors.collect {
                AnnotationMirror annotationMirror -> annotationMirror.toString()
            }.findAll {
                String annotation ->
                    !annotation.startsWith('@com.junbo.langur.core.RestResource') &&
                    !annotation.startsWith('@com.wordnik.swagger.annotations')
        }.toList()

        restAdapter.restMethods = []
        ElementFilter.methodsIn(elementUtils.getAllMembers(mapperType)).each {
            ExecutableElement executableElement ->
                if (executableElement.enclosingElement.toString() == 'java.lang.Object') {
                    return
                }

                if (!(executableElement.returnType instanceof DeclaredType)) {
                    throw new ProcessingException
                    ("returnType $executableElement.returnType must be a DeclaredType.", executableElement)
                }

                def returnType = (DeclaredType) executableElement.returnType
                def returnTypeElement = returnType.asElement()
                if (returnTypeElement.toString() != 'com.junbo.langur.core.promise.Promise') {
                    throw new ProcessingException
                    ("returnType $executableElement.returnType must be a com.junbo.langur.core.promise.Promise.",
                            executableElement)
                }

                def restMethod = new RestMethodModel()

                restMethod.adapteeName = restAdapter.adapteeName
                restMethod.adapteeType = restAdapter.adapteeType
                restMethod.returnType = returnType.typeArguments[0].toString()
                restMethod.methodName = executableElement.simpleName.toString()
                restMethod.annotations = executableElement.annotationMirrors.collect {
                    AnnotationMirror annotationMirror -> annotationMirror.toString()
                }.findAll {
                    String annotation -> !annotation.startsWith('@com.wordnik.swagger.annotations')
                }.toList()

                restMethod.parameters = executableElement.parameters.collect {
                    VariableElement variableElement ->
                        new RestParameterModel(
                                paramName:variableElement.toString(),
                                paramType:variableElement.asType().toString(),
                                annotations:variableElement.annotationMirrors.collect {
                                    AnnotationMirror annotationMirror -> annotationMirror.toString()
                                })
                }

                def routeBy = executableElement.getAnnotation(RouteBy)
                restMethod.routeParamExprs = Arrays.asList(routeBy?.value() ?: new String[0])

                restMethod.authorizationNotRequired = getAuthorizationNotRequired(mapperType, executableElement)

                if (!restMethod.authorizationNotRequired) {
                    restAdapter.authorizationNotRequired = false
                }

                restAdapter.restMethods.add(restMethod)
        }

        handlerContext.restAdapter = restAdapter
    }

    private static boolean getAuthorizationNotRequired(TypeElement mapperType, ExecutableElement methodElement) {
        def authorizationNotRequired = methodElement.getAnnotation(AuthorizationNotRequired)
        if (authorizationNotRequired == null) {
            authorizationNotRequired = mapperType.getAnnotation(AuthorizationNotRequired)
        }

        if (authorizationNotRequired != null) {
            return true
        }

        return false
    }
}
