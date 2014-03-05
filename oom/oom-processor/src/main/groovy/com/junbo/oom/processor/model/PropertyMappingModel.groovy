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
class PropertyMappingModel {

    PropertyModel sourceProperty

    PropertyModel targetProperty

    String explicitMethodName

    MappingMethodRefModel mappingMethodRef

    Set<TypeModel> getImportedTypes() {
        return (sourceProperty.importedTypes + targetProperty.importedTypes + mappingMethodRef.importedTypes) as Set
    }
}
