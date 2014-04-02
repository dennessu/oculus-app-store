/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model.param

import com.junbo.langur.processor.model.ClientParameterModel
import groovy.transform.CompileStatic

/**
 * FormParameterModel.
 */
@CompileStatic
class FormParameterModel extends ClientParameterModel {
    String formName

    final String parameterType = 'form'

    Boolean collection = false

    String innerParamType
}
