/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.processor

import com.junbo.oom.processor.model.CollectionMappingMethodModel
import com.junbo.oom.processor.model.MappingMethodModel
import com.junbo.oom.processor.model.MappingMethodRefModel
import com.junbo.oom.processor.source.MappingMethodInfo
import groovy.transform.CompileStatic
/**
 * Java doc.
 */
@CompileStatic
class CollectionMappingMethodProcessor implements MappingMethodProcessor {

    @Override
    boolean canProcess(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext) {
        return mappingMethodInfo.sourceParameter.type.collectionType &&
                mappingMethodInfo.returnType.collectionType
    }

    @Override
    MappingMethodModel process(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext) {

        def sourceElementType = mappingMethodInfo.sourceParameter.type.typeParameters[0]
        def targetElementType = mappingMethodInfo.returnType.typeParameters[0]
        def contextParameter = mappingMethodInfo.contextParameter

        MappingMethodRefModel elementMappingMethod = MappingMethodProcessorUtil.getOrCreateMappingMethodRef(
                sourceElementType, targetElementType, contextParameter, processorContext)

        return new CollectionMappingMethodModel(
                name:mappingMethodInfo.name,
                sourceParameter:mappingMethodInfo.sourceParameter,
                contextParameter:mappingMethodInfo.contextParameter,
                returnType:mappingMethodInfo.returnType,
                elementMappingMethod:elementMappingMethod)
    }

    final int sequenceNumber = 10
}
