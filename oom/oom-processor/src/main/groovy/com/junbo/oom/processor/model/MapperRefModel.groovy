/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.model

import groovy.transform.CompileStatic

import java.beans.Introspector
/**
 * Java doc.
 */
@CompileStatic
class MapperRefModel {

    TypeModel mapperType

    Set<TypeModel> getImportedTypes() {
        return [mapperType] as Set
    }

    String getVariableName() {
        return '__' + Introspector.decapitalize(mapperType.name)
    }
}
