package com.goshop.langur.processor.handler

import com.goshop.langur.processor.ProcessingException
import com.goshop.langur.processor.model.ClientMethodModel
import com.goshop.langur.processor.model.ClientParameterModel
import com.goshop.langur.processor.model.ClientProxyModel
import com.goshop.langur.processor.model.param.*
import groovy.transform.CompileStatic

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.ws.rs.*

@CompileStatic
class ClientProxyParser implements RestResourceHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {
        def elementUtils = handlerContext.processingEnv.elementUtils

        def clientProxy = new ClientProxyModel()
        clientProxy.packageName = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + ".proxy"
        clientProxy.className = mapperType.simpleName.toString() + "ClientProxy"

        clientProxy.interfaceType = elementUtils.getPackageOf(mapperType).qualifiedName.toString() + "." + mapperType.simpleName.toString()

        clientProxy.clientMethods = []
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

                def clientMethod = new ClientMethodModel()

                clientMethod.returnType = returnType.typeArguments[0].toString()
                clientMethod.methodName = executableElement.simpleName.toString()

                clientMethod.parameters = executableElement.parameters.collect {
                    VariableElement variableElement ->
                        createParameter(elementUtils, variableElement)
                }

                clientMethod.httpMethodName = normalizeHttpMethodName(getHttpMethodName(executableElement))

                clientMethod.path = getPath(mapperType, executableElement)

                clientMethod.contentType = getContentType(mapperType, executableElement)

                clientMethod.accepts = getAccepts(mapperType, executableElement)

                clientProxy.clientMethods.add(clientMethod)
        }

        handlerContext.clientProxy = clientProxy
    }

    private static String getHttpMethodName(ExecutableElement methodElement) {

        def httpMethod = methodElement.getAnnotation(HttpMethod.class)
        if (httpMethod != null) {
            return httpMethod.value()
        }

        for (AnnotationMirror annotationMirror : methodElement.annotationMirrors as List<AnnotationMirror>) {
            def annotatedMethod = annotationMirror.annotationType.asElement().getAnnotation(HttpMethod.class)
            if (annotatedMethod != null) {
                return annotatedMethod.value()
            }
        }

        throw new ProcessingException("Unable to get http method name", methodElement)
    }

    private static normalizeHttpMethodName(String methodName) {
        return methodName.substring(0, 1).toUpperCase() + methodName.substring(1).toLowerCase()
    }

    private static String getPath(TypeElement mapperType, ExecutableElement methodElement) {

        String path = "";

        def pathAnnotation = mapperType.getAnnotation(Path.class)
        if (pathAnnotation != null) {
            path += pathAnnotation.value();
        }

        pathAnnotation = methodElement.getAnnotation(Path.class)
        if (pathAnnotation != null) {
            path += pathAnnotation.value();
        }

        return path;
    }

    private static String getContentType(TypeElement mapperType, ExecutableElement methodElement) {

        def consumes = methodElement.getAnnotation(Consumes.class)
        if (consumes == null) {
            consumes = mapperType.getAnnotation(Consumes.class)
        }

        if (consumes != null && consumes.value() != null && consumes.value().length > 0) {
            return consumes.value()[0];
        }

        return null;
    }

    private static List<String> getAccepts(TypeElement mapperType, ExecutableElement methodElement) {

        def produces = methodElement.getAnnotation(Produces.class)
        if (produces == null) {
            produces = mapperType.getAnnotation(Produces.class)
        }

        if (produces != null && produces.value() != null && produces.value().length > 0) {
            return produces.value() as List
        }

        return [] as List
    }

    private static ClientParameterModel createParameter(Elements elementUtils, VariableElement variableElement) {
        QueryParam queryParam = variableElement.getAnnotation(QueryParam.class)
        if (queryParam != null) {
            return new QueryParameterModel(
                    paramType: variableElement.asType().toString(),
                    paramName: variableElement.toString(),
                    queryName: queryParam.value()
            )
        }

        PathParam pathParam = variableElement.getAnnotation(PathParam.class)
        if (pathParam != null) {
            return new PathParameterModel(
                    paramType: variableElement.asType().toString(),
                    paramName: variableElement.toString(),
                    pathName: pathParam.value()
            )
        }

        HeaderParam headerParam = variableElement.getAnnotation(HeaderParam.class)
        if (headerParam != null) {
            return new HeaderParameterModel(
                    paramType: variableElement.asType().toString(),
                    paramName: variableElement.toString(),
                    headerName: headerParam.value()
            )
        }

        BeanParam beanParam = variableElement.getAnnotation(BeanParam.class)
        if (beanParam != null) {
            return new BeanParameterModel(
                    paramType: variableElement.asType().toString(),
                    paramName: variableElement.toString(),
                    parameters: getBeanParameters(elementUtils, variableElement.toString(), variableElement.asType())
            )
        }

        return new EntityParameterModel(
                paramType: variableElement.asType().toString(),
                paramName: variableElement.toString(),
        )
    }

    private
    static List<ClientParameterModel> getBeanParameters(Elements elementUtils, String variableName, TypeMirror variableType) {

        def typeElement = (TypeElement) ((DeclaredType) variableType).asElement()

        def result = []
        ElementFilter.methodsIn(elementUtils.getAllMembers(typeElement)).each { ExecutableElement executableElement ->
            if (executableElement.enclosingElement.toString() == "java.lang.Object") {
                return
            }

            QueryParam queryParam = executableElement.getAnnotation(QueryParam.class)
            if (queryParam != null) {
                result.add(new QueryParameterModel(
                        paramType: executableElement.getReturnType().toString(),
                        paramName: variableName + "." + executableElement.simpleName.toString() + "()",
                        queryName: queryParam.value()
                ))
                return
            }

            PathParam pathParam = executableElement.getAnnotation(PathParam.class)
            if (pathParam != null) {
                result.add(new PathParameterModel(
                        paramType: executableElement.returnType.toString(),
                        paramName: variableName + "." + executableElement.simpleName.toString() + "()",
                        pathName: pathParam.value()
                ))
                return
            }

            HeaderParam headerParam = executableElement.getAnnotation(HeaderParam.class)
            if (headerParam != null) {
                result.add(new HeaderParameterModel(
                        paramType: executableElement.returnType.toString(),
                        paramName: variableName + "." + executableElement.simpleName.toString() + "()",
                        headerName: headerParam.value()
                ))
                return
            }
        }

        return result
    }
}
