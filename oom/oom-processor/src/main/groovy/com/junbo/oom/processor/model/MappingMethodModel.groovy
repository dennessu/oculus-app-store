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
class MappingMethodModel {

    String name

    ParameterModel sourceParameter

    ParameterModel alternativeSourceParameter

    ParameterModel contextParameter

    TypeModel returnType

    Set<TypeModel> getImportedTypes() {
        def result = []

        result.addAll(sourceParameter.type.importedTypes)

        if (contextParameter != null) {
            result.addAll(contextParameter.type.importedTypes)
        }

        result.addAll(returnType.importedTypes)

        return result as Set
    }
}
