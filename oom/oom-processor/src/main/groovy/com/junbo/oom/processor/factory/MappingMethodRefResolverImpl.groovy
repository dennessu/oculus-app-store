/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.factory

import com.junbo.oom.processor.model.MappingMethodRefModel
import com.junbo.oom.processor.model.TypeModel
import com.junbo.oom.processor.util.Constants
import groovy.transform.CompileStatic
import org.springframework.util.StringUtils

/**
 * Java doc.
 */
@CompileStatic
class MappingMethodRefResolverImpl implements MappingMethodRefResolver {

    private final Map<Tuple, MappingMethodRefModel> mappingMethodRefs = [:]
    private final Map<Tuple, MappingMethodRefModel> explicitMappingMethodRefs = [:]

    @Override
    MappingMethodRefModel resolve(TypeModel sourceType, TypeModel targetType,
                                  boolean hasAlternativeSourceParameter, boolean hasContextParameter,
                                  String explicitMappingMethod) {

        if (StringUtils.isEmpty(explicitMappingMethod)) {

            return mappingMethodRefs[new Tuple(
                    sourceType,
                    targetType,
                    hasAlternativeSourceParameter,
                    hasContextParameter)] ?: // fallback to the method without context parameter.
                    mappingMethodRefs[new Tuple(
                            sourceType,
                            targetType,
                            hasAlternativeSourceParameter,
                            false)]
        }

        return explicitMappingMethodRefs[new Tuple(
                sourceType,
                targetType,
                hasAlternativeSourceParameter,
                hasContextParameter,
                explicitMappingMethod)] ?: // fallback to the method without context parameter.
                explicitMappingMethodRefs[new Tuple(
                        sourceType,
                        targetType,
                        hasAlternativeSourceParameter,
                        false,
                        explicitMappingMethod)]
    }

    @Override
    void register(TypeModel sourceType, TypeModel targetType, MappingMethodRefModel mappingMethodRef) {

        if (mappingMethodRef.name.startsWith(Constants.EXPLICIT_METHOD_NAME_PREFIX)) {
            explicitMappingMethodRefs[new Tuple(
                    sourceType,
                    targetType,
                    mappingMethodRef.hasAlternativeSourceParameter,
                    mappingMethodRef.hasContextParameter,
                    mappingMethodRef.name)] = mappingMethodRef

            return;
        }

        mappingMethodRefs[new Tuple(
                sourceType,
                targetType,
                mappingMethodRef.hasAlternativeSourceParameter,
                mappingMethodRef.hasContextParameter)] = mappingMethodRef
    }
}
