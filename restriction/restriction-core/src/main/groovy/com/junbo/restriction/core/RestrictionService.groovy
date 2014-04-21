/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core

import com.junbo.langur.core.promise.Promise
import com.junbo.restriction.spec.model.AgeCheck

/**
 * Interface of RestrictionService.
 */
interface RestrictionService {
    Promise<AgeCheck> getAgeCheck(AgeCheck ageCheck)
}