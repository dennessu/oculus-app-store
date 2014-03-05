/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.factory
import com.junbo.oom.processor.ProcessingException
import com.junbo.oom.processor.model.TypeModel
import groovy.transform.CompileStatic

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
/**
 * Java doc.
 */
@CompileStatic
class TypeFactoryImpl implements TypeFactory {
    private static final String UTIL_PACKAGENAME = 'java.util'
    private static final String UTIL_ARRAYLIST_NAME = 'ArrayList'

    private final ProcessingEnvironment processingEnv

    private final TypeMirror collectionType

    private final TypeMirror mapType

    TypeFactoryImpl(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv

        collectionType = processingEnv.elementUtils.getTypeElement(Collection.canonicalName).asType()
        collectionType = processingEnv.typeUtils.erasure(collectionType)

        mapType = processingEnv.elementUtils.getTypeElement(Map.canonicalName).asType()
        mapType = processingEnv.typeUtils.erasure(mapType)
    }

    @Override
    TypeModel getType(TypeMirror typeMirror) {
        if (typeMirror.kind != TypeKind.DECLARED) {
            throw new ProcessingException("Type $typeMirror of kind $typeMirror.kind is not supported.")
        }

        TypeElement typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement()
        def packageElement = processingEnv.elementUtils.getPackageOf(typeElement)

        def type = new TypeModel(
                packageName:packageElement.qualifiedName.toString(),
                name:typeElement.simpleName.toString(),
                typeParameters:getTypeParameters(typeMirror),
                collectionType:processingEnv.typeUtils.isSubtype(typeMirror, collectionType),
                mapType:processingEnv.typeUtils.isSubtype(typeMirror, mapType)
        )

        type.implementationType = getImplementationType(type)

        return type
    }

    private List<TypeModel> getTypeParameters(TypeMirror mirror) {
        def declaredType = (DeclaredType) mirror

        return declaredType.typeArguments.collect {
            TypeMirror typeParameter -> getType(typeParameter)
        }
    }

    private static TypeModel getImplementationType(TypeModel typeModel) {

        def packageName
        def name

        switch (typeModel.qualifiedName) {
            case 'java.util.Collection':
                packageName = UTIL_PACKAGENAME
                name = UTIL_ARRAYLIST_NAME
                break

            case 'java.util.List':
                packageName = UTIL_PACKAGENAME
                name = UTIL_ARRAYLIST_NAME
                break

            case 'java.util.Set':
                packageName = UTIL_PACKAGENAME
                name = 'HashSet'
                break

            case 'java.util.Map':
                packageName = UTIL_PACKAGENAME
                name = 'HashMap'
                break

            default:
                return null
        }

        return new TypeModel(
                packageName:packageName,
                name:name,
                typeParameters:typeModel.typeParameters,
                collectionType:typeModel.collectionType,
                mapType:typeModel.mapType
        )
    }
}
