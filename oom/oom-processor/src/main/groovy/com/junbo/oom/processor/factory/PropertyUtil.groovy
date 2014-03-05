/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.factory

import com.junbo.oom.processor.model.TypeModel
import groovy.transform.CompileStatic

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.ExecutableType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import java.beans.Introspector
import java.util.concurrent.ConcurrentHashMap
/**
 * Java doc.
 */
@CompileStatic
class PropertyUtil {
    private static final Integer MIN_SET_GET_METHOD_LENGTH = 3
    private static final Integer MIN_IS_METHOD_LENGTH = 2

    private static final Map<String, Map<String, ExecutableElement>> SETTERS_BY_TYPE = new ConcurrentHashMap<>()

    private static final Map<String, Map<String, ExecutableElement>> GETTERS_BY_TYPE = new ConcurrentHashMap<>()

    static Map<String, ExecutableElement> getSetters(TypeModel type, ProcessingEnvironment processingEnv) {

        def result = SETTERS_BY_TYPE[type.qualifiedName]
        if (result != null) {
            return result
        }

        def typeElement = processingEnv.elementUtils.getTypeElement(type.qualifiedName)

        result = processingEnv.elementUtils.getAllMembers(typeElement).findAll { Element member ->
            (member instanceof ExecutableElement) && isSetterMethod((ExecutableElement) member)
        }.collectEntries {
            ExecutableElement executable ->
                [
                        getPropertyName(executable),
                        executable
                ]
        }

        SETTERS_BY_TYPE[type.qualifiedName] = result
        return result
    }

    static Map<String, ExecutableElement> getGetters(TypeModel type, ProcessingEnvironment processingEnv) {

        def result = GETTERS_BY_TYPE[type.qualifiedName]
        if (result != null) {
            return result
        }

        def typeElement = processingEnv.elementUtils.getTypeElement(type.qualifiedName)

        result = processingEnv.elementUtils.getAllMembers(typeElement).findAll { Element member ->
            (member instanceof ExecutableElement) && isGetterMethod((ExecutableElement) member)
        }.collectEntries {
            ExecutableElement executable ->
                [
                        getPropertyName(executable),
                        executable
                ]
        }

        GETTERS_BY_TYPE[type.qualifiedName] = result
        return result
    }

    static TypeMirror getSetterType(TypeModel type, ExecutableElement executableElement,
                                    ProcessingEnvironment processingEnv) {
        def typeElement = processingEnv.elementUtils.getTypeElement(type.qualifiedName)

        def executableType = (ExecutableType) processingEnv.typeUtils.asMemberOf((DeclaredType) typeElement.asType(),
                executableElement)

        return executableType.parameterTypes[0]
    }

    static TypeMirror getGetterType(TypeModel type, ExecutableElement executableElement,
                                    ProcessingEnvironment processingEnv) {
        def typeElement = processingEnv.elementUtils.getTypeElement(type.qualifiedName)

        def executableType = (ExecutableType) processingEnv.typeUtils.asMemberOf((DeclaredType) typeElement.asType(),
                executableElement)

        return executableType.returnType
    }

    static boolean isGetterMethod(ExecutableElement method) {
        return isNonBooleanGetterMethod(method) || isBooleanGetterMethod(method)
    }

    private static boolean isNonBooleanGetterMethod(ExecutableElement method) {
        def name = method.simpleName.toString()

        return method.parameters.isEmpty() &&
                name.startsWith('get') && name.length() > MIN_SET_GET_METHOD_LENGTH &&
                method.returnType.kind != TypeKind.VOID
    }

    private static boolean isBooleanGetterMethod(ExecutableElement method) {
        def name = method.simpleName.toString()

        return method.parameters.isEmpty() &&
                name.startsWith('is') && name.length() > MIN_IS_METHOD_LENGTH &&
                method.returnType.kind == TypeKind.BOOLEAN
    }

    static boolean isSetterMethod(ExecutableElement method) {
        def name = method.simpleName.toString()

        return name.startsWith('set') && name.length() > MIN_SET_GET_METHOD_LENGTH &&
                method.parameters.size() == 1 &&
                method.returnType.kind == TypeKind.VOID
    }

    static String getPropertyName(ExecutableElement method) {
        if (isNonBooleanGetterMethod(method)) {
            return Introspector.decapitalize(
                    method.simpleName.toString()[MIN_SET_GET_METHOD_LENGTH..method.simpleName.toString().length() - 1])
        }
        else if (isBooleanGetterMethod(method)) {
            return Introspector.decapitalize(
                    method.simpleName.toString()[MIN_IS_METHOD_LENGTH..method.simpleName.toString().length() - 1])
        }
        else if (isSetterMethod(method)) {
            return Introspector.decapitalize(
                    method.simpleName.toString()[MIN_SET_GET_METHOD_LENGTH..method.simpleName.toString().length() - 1])
        }

        throw new IllegalArgumentException(method.toString() + ' is not a getter nor a setter.')
    }
}
