/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.processor

import com.junbo.oom.processor.model.MapMappingMethodModel
import com.junbo.oom.processor.model.MappingMethodModel
import com.junbo.oom.processor.model.MappingMethodRefModel
import com.junbo.oom.processor.source.MappingMethodInfo
import groovy.transform.CompileStatic
/**
 * Java doc.
 */
@CompileStatic
class MapMappingMethodProcessor implements MappingMethodProcessor {

    @Override
    boolean canProcess(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext) {
        return mappingMethodInfo.sourceParameter.type.mapType &&
                mappingMethodInfo.returnType.mapType
    }

    @Override
    MappingMethodModel process(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext) {

        def sourceKeyType = mappingMethodInfo.sourceParameter.type.typeParameters[0]
        def targetKeyType = mappingMethodInfo.returnType.typeParameters[0]

        def sourceValueType = mappingMethodInfo.sourceParameter.type.typeParameters[1]
        def targetValueType = mappingMethodInfo.returnType.typeParameters[1]

        def contextParameter = mappingMethodInfo.contextParameter

        MappingMethodRefModel keyMappingMethod = MappingMethodProcessorUtil.getOrCreateMappingMethodRef(
                sourceKeyType, targetKeyType, contextParameter, processorContext)

        MappingMethodRefModel valueMappingMethod = MappingMethodProcessorUtil.getOrCreateMappingMethodRef(
                sourceValueType, targetValueType, contextParameter, processorContext)

        return new MapMappingMethodModel(
                name:mappingMethodInfo.name,
                sourceParameter:mappingMethodInfo.sourceParameter,
                contextParameter:mappingMethodInfo.contextParameter,
                returnType:mappingMethodInfo.returnType,
                keyMappingMethod:keyMappingMethod,
                valueMappingMethod:valueMappingMethod)
    }

    final int sequenceNumber = 20
}
