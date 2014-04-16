/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.restriction.core.validator

import com.junbo.restriction.spec.model.AgeCheck

/**
 * Interface of RestrictionValidator.
 */
interface RestrictionValidator {
    void validate(AgeCheck ageCheck)
//    void validate(User user)
}