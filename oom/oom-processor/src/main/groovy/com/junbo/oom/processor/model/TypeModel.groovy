/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.model

import groovy.transform.CompileStatic
/**
 * Java doc.
 */
@CompileStatic
class TypeModel {

    private static final Integer PRIMITIVE_VALUE = 31
    private static final Integer INIT_VALUE = 0

    String packageName

    String name

    List<TypeModel> typeParameters

    TypeModel implementationType

    boolean collectionType

    boolean mapType

    String getQualifiedName() {
        return packageName == null ? name : packageName + '.' + name
    }


    Set<TypeModel> getImportedTypes() {
        def result = [this]

        if (implementationType != null) {
            result.add(implementationType)
        }

        if (typeParameters != null) {
            result.addAll(typeParameters)
        }

        return result as Set
    }

    boolean equals(o) {
        if (this.is(o)) {
            return true
        }
        if (getClass() != o.class) {
            return false
        }

        TypeModel typeModel = (TypeModel) o

        if (name != typeModel.name) {
            return false
        }
        if (packageName != typeModel.packageName) {
            return false
        }
        if (typeParameters != typeModel.typeParameters) {
            return false
        }

        return true
    }

    int hashCode() {
        int result
        result = (packageName != null ? packageName.hashCode() : INIT_VALUE)
        result = PRIMITIVE_VALUE * result + (name != null ? name.hashCode() : INIT_VALUE)
        result = PRIMITIVE_VALUE * result + (typeParameters != null ? typeParameters.hashCode() : INIT_VALUE)
        return result
    }


    @Override
    String toString() {
        if (typeParameters == null || typeParameters.isEmpty()) {
            return qualifiedName
        }

        return "$qualifiedName$typeParameters"
    }
}
