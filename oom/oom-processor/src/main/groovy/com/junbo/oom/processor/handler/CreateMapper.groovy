/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.handler

import com.junbo.oom.processor.MapperPrism
import com.junbo.oom.processor.ProcessingException
import com.junbo.oom.processor.model.MapperModel
import com.junbo.oom.processor.model.MapperRefModel
import com.junbo.oom.processor.model.MappingMethodRefModel
import com.junbo.oom.processor.processor.MappingMethodProcessor
import com.junbo.oom.processor.processor.ProcessorContext
import com.junbo.oom.processor.source.MappingMethodInfo
import groovy.transform.CompileStatic

import javax.lang.model.element.TypeElement
/**
 * Java doc.
 */
@CompileStatic
class CreateMapper implements MapperHandler {

    @Override
    void handle(TypeElement mapperType, HandlerContext handlerContext) {

        MapperPrism mapperPrism = MapperPrism.getInstanceOn(mapperType)
        assert mapperPrism != null: 'mapperPrism is null'

        def type = handlerContext.typeFactory.getType(mapperType.asType())

        handlerContext.mapperModel = new MapperModel(
            packageName:type.packageName,
            interfaceName:type.name,
            implementationName:type.name + 'Impl',
        )

        handlerContext.mapperModel.mapperRefs = handlerContext.mapperRefs.collect { TypeElement typeElement ->
            new MapperRefModel(
                    mapperType:handlerContext.typeFactory.getType(typeElement.asType())
            )
        }

        handlerContext.mapperModel.mappingMethods = []

        def methodsToImpl = [] as List<MappingMethodInfo>

        handlerContext.mappingMethods.each { MappingMethodInfo mappingMethod ->
            MapperRefModel declaringMapper = mappingMethod.declaringMapper == null ?
                null : new MapperRefModel(mapperType:mappingMethod.declaringMapper)

            handlerContext.mappingMethodRefResolver.register(
                    mappingMethod.sourceParameter.type,
                    mappingMethod.returnType,
                    new MappingMethodRefModel(
                            declaringMapper: declaringMapper,
                            name: mappingMethod.name,
                            hasAlternativeSourceParameter: mappingMethod.alternativeSourceParameter != null,
                            hasContextParameter: mappingMethod.contextParameter != null
                    )
            )

            if (mappingMethod.declaringMapper == null) {
                methodsToImpl.add(mappingMethod)
            }
        }

        def processors = fetchProcessors()

        while (!methodsToImpl.isEmpty()) {
            def methodToImpl = methodsToImpl.pop()

            def processorContext = new ProcessorContext(
                    handlerContext:handlerContext,
                    onNewMappingMethod: { MappingMethodInfo newMethod -> methodsToImpl.push(newMethod) }
            )

            def processor = processors.find { MappingMethodProcessor processor ->
                processor.canProcess(methodToImpl, processorContext)
            }

            if (processor != null) {
                def mappingMethod = processor.process(methodToImpl, processorContext)
                handlerContext.mapperModel.mappingMethods.add(mappingMethod)
            } else {
                throw new ProcessingException('Unable to process Mapping Method ' + methodToImpl)
            }
        }
    }

    final int sequenceNumber = 20

    private static List<MappingMethodProcessor> fetchProcessors() {
        return ServiceLoader.load(MappingMethodProcessor, MappingMethodProcessor.classLoader).iterator().toList().sort {
            MappingMethodProcessor o1, MappingMethodProcessor o2 -> o1.sequenceNumber - o2.sequenceNumber
        }
    }
}
