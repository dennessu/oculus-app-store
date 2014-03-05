/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.oom.processor.source

import com.junbo.oom.processor.model.ParameterModel
import com.junbo.oom.processor.model.TypeModel
import groovy.transform.CompileStatic
/**
 * Java doc.
 */
@CompileStatic
class MappingMethodInfo {

    TypeModel declaringMapper

    String name

    TypeModel returnType

    ParameterModel sourceParameter

    ParameterModel contextParameter

    List<MappingPropertyInfo> properties

    @Override
    String toString() {
        return 'MappingMethodInfo{' +
                'declaringMapper=' + declaringMapper +
                ', name=\'' + name + '\'' +
                ', returnType=' + returnType +
                ', sourceParameter=' + sourceParameter +
                ', contextParameter=' + contextParameter +
                '}'
    }
}
