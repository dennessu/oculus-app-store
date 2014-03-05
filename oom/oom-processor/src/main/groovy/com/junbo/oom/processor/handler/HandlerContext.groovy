/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.handler

import com.junbo.oom.processor.factory.MappingMethodRefResolver
import com.junbo.oom.processor.factory.PropertyFactory
import com.junbo.oom.processor.factory.TypeFactory
import com.junbo.oom.processor.model.MapperModel
import com.junbo.oom.processor.source.MappingMethodInfo
import groovy.transform.CompileStatic

import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
/**
 * Java doc.
 */
@CompileStatic
class HandlerContext {

    List<TypeElement> mapperRefs

    List<MappingMethodInfo> mappingMethods

    MapperModel mapperModel

    ProcessingEnvironment processingEnv

    TypeFactory typeFactory

    PropertyFactory propertyFactory

    MappingMethodRefResolver mappingMethodRefResolver
}