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
class MappingMethodRefModel {

    MapperRefModel declaringMapper

    String name

    boolean hasContextParameter

    Set<TypeModel> getImportedTypes() {
        def result = declaringMapper == null ? [] : declaringMapper.importedTypes

        return (result) as Set
    }
}
