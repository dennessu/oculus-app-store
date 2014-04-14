/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.factory

import com.junbo.oom.processor.model.MappingMethodRefModel
import com.junbo.oom.processor.model.TypeModel

/**
 * Java doc.
 */
interface MappingMethodRefResolver {

    MappingMethodRefModel resolve(TypeModel sourceType, TypeModel targetType,
                                  boolean hasAlternativeSourceParameter, boolean hasContextParameter,
                                  String explicitMappingMethod)

    void register(TypeModel sourceType, TypeModel targetType, MappingMethodRefModel mappingMethodRef)
}
