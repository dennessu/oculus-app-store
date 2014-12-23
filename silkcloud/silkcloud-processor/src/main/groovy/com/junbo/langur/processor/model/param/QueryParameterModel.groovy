/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model.param

import com.junbo.langur.processor.model.ClientParameterModel
import groovy.transform.CompileStatic

/**
 * Created by kevingu on 11/28/13.
 */
@CompileStatic
class QueryParameterModel extends ClientParameterModel {

    String queryName

    final String parameterType = 'query'

    Boolean collection = false

    String innerParamType
}
