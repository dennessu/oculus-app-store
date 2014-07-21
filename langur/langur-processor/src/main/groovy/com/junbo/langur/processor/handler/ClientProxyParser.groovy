/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.handler

import com.junbo.langur.core.AuthorizationNotRequired
import com.junbo.langur.core.InProcessCallable
import com.junbo.langur.processor.ProcessingException
import com.junbo.langur.processor.model.ClientMethodModel
import com.junbo.langur.processor.model.ClientParameterModel
import com.junbo.langur.processor.model.ClientProxyModel
import com.junbo.langur.processor.model.param.*
import groovy.transform.CompileStatic

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.ws.rs.*
/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class ClientProxyParser implements RestResourceHandler {
    private static final String OBJECT_TYPE = 'java.lang.Object'
    private static final String BRACET = '()'
    private static final String GET = 'get'
    private static final String IS = 'is'
    private static final String PRIMARY_BOOLEAN = 'boolean'
    private static final String DOT = '.'

    private static TypeMirror collectionType

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def elementUtils = handlerContext.processingEnv.elementUtils
        def typeUtils = handlerContext.processingEnv.typeUtils

        collectionType = elementUtils.getTypeElement(Collection.canonicalName).asType()
        collectionType = typeUtils.erasure(collectionType)

        def clientProxy = new ClientProxyModel()
        clientProxy.packageName = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + '.proxy'
        clientProxy.className = mapperType.simpleName.toString() + 'ClientProxy'

        clientProxy.interfaceType = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + DOT +
                mapperType.simpleName.toString()

        clientProxy.interfaceSimpleType = mapperType.simpleName.toString()

        clientProxy.clientMethods = []
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

                def clientMethod = new ClientMethodModel()

                clientMethod.interfaceType = clientProxy.interfaceType
                clientMethod.returnType = returnType.typeArguments[0].toString()
                clientMethod.methodName = executableElement.simpleName.toString()

                clientMethod.parameters = executableElement.parameters.collect {
                    VariableElement variableElement ->
                        intiParameter(elementUtils, typeUtils, variableElement)
                }

                clientMethod.httpMethodName = normalizeHttpMethodName(getHttpMethodName(executableElement))

                clientMethod.path = getPath(mapperType, executableElement)

                clientMethod.contentType = getContentType(mapperType, executableElement)

                clientMethod.accepts = getAccepts(mapperType, executableElement)

                clientMethod.inProcessCallable = getInProcessCallable(mapperType, executableElement)

                clientMethod.authorizationNotRequired = getAuthorizationNotRequired(mapperType, executableElement)

                clientProxy.clientMethods.add(clientMethod)
        }

        handlerContext.clientProxy = clientProxy
    }

    private static String getHttpMethodName(ExecutableElement methodElement) {

        def httpMethod = methodElement.getAnnotation(HttpMethod)
        if (httpMethod != null) {
            return httpMethod.value()
        }

        for (AnnotationMirror annotationMirror : methodElement.annotationMirrors as List<AnnotationMirror>) {
            def annotatedMethod = annotationMirror.annotationType.asElement().getAnnotation(HttpMethod)
            if (annotatedMethod != null) {
                return annotatedMethod.value()
            }
        }

        throw new ProcessingException('Unable to get http method name', methodElement)
    }

    private static normalizeHttpMethodName(String methodName) {
        return methodName[(0)].toUpperCase() + methodName[1..methodName.length() - 1].toLowerCase()
    }

    private static String getPath(TypeElement mapperType, ExecutableElement methodElement) {

        String path = ''

        def pathAnnotation = mapperType.getAnnotation(Path)
        if (pathAnnotation != null) {
            path += pathAnnotation.value()
        }

        pathAnnotation = methodElement.getAnnotation(Path)
        if (pathAnnotation != null) {
            path += pathAnnotation.value()
        }

        return path
    }

    private static String getContentType(TypeElement mapperType, ExecutableElement methodElement) {

        def consumes = methodElement.getAnnotation(Consumes)
        if (consumes == null) {
            consumes = mapperType.getAnnotation(Consumes)
        }

        if (consumes != null && consumes.value() != null && consumes.value().length > 0) {
            return consumes.value()[0]
        }

        return null
    }

    private static List<String> getAccepts(TypeElement mapperType, ExecutableElement methodElement) {

        def produces = methodElement.getAnnotation(Produces)
        if (produces == null) {
            produces = mapperType.getAnnotation(Produces)
        }

        if (produces != null && produces.value() != null && produces.value().length > 0) {
            return produces.value() as List
        }

        return [] as List
    }

    private static boolean getInProcessCallable(TypeElement mapperType, ExecutableElement methodElement) {

        def produces = methodElement.getAnnotation(InProcessCallable)
        if (produces == null) {
            produces = mapperType.getAnnotation(InProcessCallable)
        }

        if (produces != null) {
            return !produces.disable();
        }

        return true
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

    private static String parseInnerParamType(TypeMirror typeMirror) {
        if (typeMirror instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) typeMirror
            if (!declaredType.typeArguments.empty) {
                return declaredType.typeArguments[0]
            }

            return null
        }

        return null
    }

    private
    static ClientParameterModel intiParameter(Elements elementUtils, Types typeUtils, VariableElement variableElement) {
        QueryParam queryParam = variableElement.getAnnotation(QueryParam)
        if (queryParam != null) {
            return new QueryParameterModel(
                    paramType:variableElement.asType().toString(),
                    paramName:variableElement.toString(),
                    queryName: queryParam.value(),
                    collection: typeUtils.isSubtype(variableElement.asType(), collectionType),
                    innerParamType: parseInnerParamType(variableElement.asType())
            )
        }

        PathParam pathParam = variableElement.getAnnotation(PathParam)
        if (pathParam != null) {
            return new PathParameterModel(
                    paramType:variableElement.asType().toString(),
                    paramName:variableElement.toString(),
                    pathName:pathParam.value()
            )
        }

        HeaderParam headerParam = variableElement.getAnnotation(HeaderParam)
        if (headerParam != null) {
            return new HeaderParameterModel(
                    paramType:variableElement.asType().toString(),
                    paramName:variableElement.toString(),
                    headerName: headerParam.value(),
                    collection: typeUtils.isSubtype(variableElement.asType(), collectionType),
                    innerParamType: parseInnerParamType(variableElement.asType())
            )
        }

        BeanParam beanParam = variableElement.getAnnotation(BeanParam)
        if (beanParam != null) {
            return new BeanParameterModel(
                    paramType:variableElement.asType().toString(),
                    paramName:variableElement.toString(),
                    parameters: getBeanParameters(elementUtils, typeUtils, variableElement.toString(),
                            variableElement.asType())
            )
        }

        FormParam formParam = variableElement.getAnnotation(FormParam)
        if (formParam != null) {
            return new FormParameterModel(
                    paramType: variableElement.asType().toString(),
                    paramName: variableElement.toString(),
                    formName: formParam.value(),
                    collection: typeUtils.isSubtype(variableElement.asType(), collectionType),
                    innerParamType: parseInnerParamType(variableElement.asType())
            )
        }

        return new EntityParameterModel(
                paramType:variableElement.asType().toString(),
                paramName:variableElement.toString(),
        )
    }

    private static List<ClientParameterModel> getBeanParameters(Elements elementUtils, Types typeUtils,
                                                                String variableName, TypeMirror variableType) {
        def simpleNames = []
        def result = []

        def typeElement = (TypeElement) ((DeclaredType) variableType).asElement()
        ElementFilter.fieldsIn(elementUtils.getAllMembers(typeElement)).each { VariableElement variableElement ->
            String fieldGetMethodName = getGetMethodName(variableElement)
            QueryParam queryParam = variableElement.getAnnotation(QueryParam)
            if (queryParam != null && !simpleNames.contains(fieldGetMethodName)) {
                result.add(new QueryParameterModel(
                        paramType:variableElement.asType().toString(),
                        paramName:variableName + DOT + fieldGetMethodName + BRACET,
                        queryName: queryParam.value(),
                        collection: typeUtils.isSubtype(variableElement.asType(), collectionType),
                        innerParamType: parseInnerParamType(variableElement.asType())
                ))
                simpleNames.add(fieldGetMethodName)
                return
            }

            PathParam pathParam = variableElement.getAnnotation(PathParam)
            if (pathParam != null && !simpleNames.contains(fieldGetMethodName)) {
                result.add(new PathParameterModel(
                        paramType:variableElement.asType().toString(),
                        paramName:variableName + DOT + fieldGetMethodName + BRACET,
                        pathName:pathParam.value()
                ))
                simpleNames.add(fieldGetMethodName)
                return
            }

            HeaderParam headerParam = variableElement.getAnnotation(HeaderParam)
            if (headerParam != null && !simpleNames.contains(fieldGetMethodName)) {
                result.add(new HeaderParameterModel(
                        paramType:variableElement.asType().toString(),
                        paramName:variableName + DOT + fieldGetMethodName + BRACET,
                        headerName: headerParam.value(),
                        collection: typeUtils.isSubtype(variableElement.asType(), collectionType),
                        innerParamType: parseInnerParamType(variableElement.asType())
                ))
                simpleNames.add(fieldGetMethodName)
                return
            }
            FormParam formParam = variableElement.getAnnotation(FormParam)
            if (formParam != null && !simpleNames.contains(fieldGetMethodName)) {
                result.add(new FormParameterModel(
                        paramType:variableElement.asType().toString(),
                        paramName:variableName + DOT + fieldGetMethodName + BRACET,
                        formName: formParam.value(),
                        collection: typeUtils.isSubtype(variableElement.asType(), collectionType),
                        innerParamType: parseInnerParamType(variableElement.asType())
                ))
                simpleNames.add(fieldGetMethodName)
                return
            }
        }
        typeElement = (TypeElement) ((DeclaredType) variableType).asElement()
        ElementFilter.methodsIn(elementUtils.getAllMembers(typeElement)).each { ExecutableElement executableElement ->
            if (executableElement.enclosingElement.toString() == OBJECT_TYPE) {
                return
            }
            QueryParam queryParam = executableElement.getAnnotation(QueryParam)
            if (queryParam != null && !simpleNames.contains(executableElement.simpleName.toString())) {
                result.add(new QueryParameterModel(
                        paramType:executableElement.returnType.toString(),
                        paramName:variableName + DOT + executableElement.simpleName.toString() + BRACET,
                        queryName: queryParam.value(),
                        collection: typeUtils.isSubtype(executableElement.returnType, collectionType),
                        innerParamType: parseInnerParamType(executableElement.returnType)
                ))
                simpleNames.add(executableElement.simpleName.toString())
                return
            }
            PathParam pathParam = executableElement.getAnnotation(PathParam)
            if (pathParam != null && !simpleNames.contains(executableElement.simpleName.toString())) {
                result.add(new PathParameterModel(
                        paramType:executableElement.returnType.toString(),
                        paramName:variableName + DOT + executableElement.simpleName.toString() + BRACET,
                        pathName:pathParam.value()
                ))
                simpleNames.add(executableElement.simpleName.toString())
                return
            }
            HeaderParam headerParam = executableElement.getAnnotation(HeaderParam)
            if (headerParam != null && !simpleNames.contains(executableElement.simpleName.toString())) {
                result.add(new HeaderParameterModel(
                        paramType:executableElement.returnType.toString(),
                        paramName:variableName + DOT + executableElement.simpleName.toString() + BRACET,
                        headerName: headerParam.value(),
                        collection: typeUtils.isSubtype(executableElement.returnType, collectionType),
                        innerParamType: parseInnerParamType(executableElement.returnType)
                ))
                simpleNames.add(executableElement.simpleName.toString())
                return
            }
            FormParam formParam = executableElement.getAnnotation(FormParam)
            if (formParam != null && !simpleNames.contains(executableElement.simpleName.toString())) {
                result.add(new FormParameterModel(
                        paramType:executableElement.returnType.toString(),
                        paramName:variableName + DOT + executableElement.simpleName.toString() + BRACET,
                        formName: formParam.value(),
                        collection: typeUtils.isSubtype(executableElement.returnType, collectionType),
                        innerParamType: parseInnerParamType(executableElement.returnType)
                ))
                simpleNames.add(executableElement.simpleName.toString())
            }
        }

        return result
    }

    static String getGetMethodName(VariableElement variableElement) {
        String name = variableElement.simpleName.toString()
        if (variableElement.asType().toString() == PRIMARY_BOOLEAN) {
            return IS + name.capitalize()
        }
        return GET + name.capitalize()
    }
}
