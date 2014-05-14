/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.core

import com.junbo.common.id.Id
import com.junbo.common.model.Link

/**
 * Created by Shenhua on 5/13/2014.
 */
interface RoleTargetParser {
    String getTargetType()

    String getFilterType()

    Id parseFilterLInk(Link filterLink)
}
