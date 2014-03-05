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
class MapperModel {

    String packageName

    String interfaceName

    String implementationName

    List<MapperRefModel> mapperRefs

    List<MappingMethodModel> mappingMethods

    Set<TypeModel> getImportedTypes() {
        return (mapperRefs.collect { MapperRefModel mapperRef -> mapperRef.importedTypes }.flatten() +
                mappingMethods.collect { MappingMethodModel mappingMethod -> mappingMethod.importedTypes }.flatten()
        ) as Set
    }

    String getFilename() {
        return packageName == null ? implementationName : packageName + '.' + implementationName
    }
}
