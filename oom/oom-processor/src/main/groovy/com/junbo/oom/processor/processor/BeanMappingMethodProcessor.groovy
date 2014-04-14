/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.processor

import com.junbo.oom.processor.factory.PropertyUtil
import com.junbo.oom.processor.model.BeanMappingMethodModel
import com.junbo.oom.processor.model.MappingMethodModel
import com.junbo.oom.processor.model.PropertyMappingModel
import com.junbo.oom.processor.source.MappingMethodInfo
import com.junbo.oom.processor.source.MappingPropertyInfo
import groovy.transform.CompileStatic

/**
 * Java doc.
 */
@CompileStatic
class BeanMappingMethodProcessor implements MappingMethodProcessor {

    @Override
    boolean canProcess(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext) {
        return true
    }

    @Override
    MappingMethodModel process(MappingMethodInfo mappingMethodInfo, ProcessorContext processorContext) {
        def handlerContext = processorContext.handlerContext
        def processingEnv = handlerContext.processingEnv
        def propertyFactory = handlerContext.propertyFactory

        def sourceType = mappingMethodInfo.sourceParameter.type
        def targetType = mappingMethodInfo.returnType
        def hasAlternativeSourceParameter = mappingMethodInfo.alternativeSourceParameter != null
        def contextParameter = mappingMethodInfo.contextParameter

        def sourceGetters = PropertyUtil.getGetters(sourceType, processingEnv)
        def targetSetters = PropertyUtil.getSetters(targetType, processingEnv)

        def matchedPropertyNames = sourceGetters.keySet().intersect(targetSetters.keySet())

        def propertyMappings = []

        mappingMethodInfo.properties.each {
            MappingPropertyInfo mappingPropertyInfo ->
                matchedPropertyNames.remove(mappingPropertyInfo.source)
                matchedPropertyNames.remove(mappingPropertyInfo.target)
                if (!mappingPropertyInfo.excluded) {
                    def sourceProperty = propertyFactory.getProperty(sourceType, mappingPropertyInfo.source)
                    def targetProperty = propertyFactory.getProperty(targetType, mappingPropertyInfo.target)

                    propertyMappings.add(new PropertyMappingModel(
                            sourceProperty: sourceProperty,
                            targetProperty: targetProperty,
                            explicitMethodName: mappingPropertyInfo.explicitMethodName))
                }
        }

        matchedPropertyNames.each { String propertyName ->
            def sourceProperty = propertyFactory.getProperty(sourceType, propertyName)
            def targetProperty = propertyFactory.getProperty(targetType, propertyName)

            propertyMappings.add(new PropertyMappingModel(
                    sourceProperty: sourceProperty,
                    targetProperty: targetProperty))
        }

        propertyMappings.each { PropertyMappingModel propertyMapping ->
            def sourceProperty = propertyMapping.sourceProperty
            def targetProperty = propertyMapping.targetProperty
            def explicitMethodName = propertyMapping.explicitMethodName

            propertyMapping.mappingMethodRef = MappingMethodProcessorUtil.getOrCreateMappingMethodRef(
                    sourceProperty.type, targetProperty.type,
                    hasAlternativeSourceParameter, contextParameter, processorContext, explicitMethodName)
        }

        return new BeanMappingMethodModel(
                name: mappingMethodInfo.name,
                sourceParameter: mappingMethodInfo.sourceParameter,
                alternativeSourceParameter: mappingMethodInfo.alternativeSourceParameter,
                contextParameter: mappingMethodInfo.contextParameter,
                returnType: mappingMethodInfo.returnType,
                propertyMappings: propertyMappings)
    }

    final int sequenceNumber = 30
}
