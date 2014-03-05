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
class BeanMappingMethodModel extends MappingMethodModel {

    List<PropertyMappingModel> propertyMappings

    Set<TypeModel> getImportedTypes() {
        return  (super.importedTypes.flatten() + propertyMappings.collect {
            PropertyMappingModel model -> model.importedTypes }.flatten()) as Set
    }
}
