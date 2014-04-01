/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.langur.processor.model.param

import com.junbo.langur.processor.model.ClientParameterModel

/**
 * Created by kevingu on 11/28/13.
 */
class HeaderParameterModel extends ClientParameterModel {

    String headerName

    final String parameterType = 'header'

    Boolean collection = false

    String innerParamType
}
