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
class MapMappingMethodModel extends MappingMethodModel {

    MappingMethodRefModel keyMappingMethod

    MappingMethodRefModel valueMappingMethod

    Set<TypeModel> getImportedTypes() {
        return  (super.importedTypes + keyMappingMethod.importedTypes + valueMappingMethod.importedTypes) as Set
    }
}
