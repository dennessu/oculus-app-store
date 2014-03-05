/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.processor

import com.junbo.oom.processor.ProcessingException
import com.junbo.oom.processor.model.MappingMethodRefModel
import com.junbo.oom.processor.model.ParameterModel
import com.junbo.oom.processor.model.TypeModel
import com.junbo.oom.processor.source.MappingMethodInfo
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Java doc.
 */
@CompileStatic
class MappingMethodProcessorUtil {

    static MappingMethodRefModel getOrCreateMappingMethodRef(
            TypeModel sourceElementType, TypeModel targetElementType, ParameterModel contextParameter,
            ProcessorContext processorContext, String explicitMethodName = null) {

        def mappingRef = processorContext.handlerContext.mappingMethodRefResolver.resolve(
                sourceElementType, targetElementType, explicitMethodName)

        if (mappingRef == null) {

            if (!StringUtils.isEmpty(explicitMethodName)) {
                throw new ProcessingException('Explicit mapping method ' + explicitMethodName + ' not found. '
                      + 'sourceElementType = ' + sourceElementType + ' targetElementType = ' + targetElementType)
            }

            def sourceTypeName = sourceElementType.name + sourceElementType.typeParameters*.name.join('')
            def targetTypeName = targetElementType.name + targetElementType.typeParameters*.name.join('')

            def newMappingMethodInfo = new MappingMethodInfo(
                    name:'__from' + sourceTypeName + 'To' + targetTypeName,
                    returnType:targetElementType,
                    sourceParameter:new ParameterModel(name:'source', type:sourceElementType),
                    contextParameter:contextParameter)

            processorContext.onNewMappingMethod?.call(newMappingMethodInfo)

            mappingRef = new MappingMethodRefModel(
                    name:newMappingMethodInfo.name,
                    hasContextParameter:contextParameter != null
            )

            processorContext.handlerContext.mappingMethodRefResolver.register(sourceElementType,
                    targetElementType, mappingRef)
        }

        return mappingRef
    }
}
